package com.adgvcxz

import android.view.View
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2018/5/3.
 */
class EventBuilder {
    private val sectionList = arrayListOf<EventSection<Any, View>>()

    @Suppress("UNCHECKED_CAST")
    fun <S, V : View> section(init: EventSection<S, V>.() -> Unit) {
        val section = EventSection<S, V>()
        section.init()
        sectionList.add(section as EventSection<Any, View>)
    }

    fun build(subject: Subject<IEvent>): List<Disposable> {
        return sectionList.map {
            it.build(subject)
        }.reduce { acc, list -> acc + list }
    }
}

class EventSection<T, V : View> {

    var filter: (Observable<T>.() -> Observable<T>)? = null
    lateinit var observable: V.() -> Observable<T>
    private val itemList = arrayListOf<IItem<T, V>>()

    fun item(init: EventItem<T, V>.() -> Unit) {
        val item = EventItem<T, V>()
        item.init()
        itemList.add(item)
    }

    fun actionItem(init: ActionItem<T, V>.() -> Unit) {
        val item = ActionItem<T, V>()
        item.init()
        itemList.add(item)
    }

    fun build(subject: Subject<IEvent>): List<Disposable> {
        return itemList.map { item ->
            when (item) {
                is EventItem -> item.view.observable()
                        .compose { filter?.invoke(it) ?: it }
                        .compose { item.filter?.invoke(it) ?: it }
                        .map { item.event.invoke(it) }
                        .subscribe { subject.onNext(it) }
                is ActionItem -> item.view.observable()
                        .compose { filter?.invoke(it) ?: it }
                        .compose { item.filter?.invoke(it) ?: it }
                        .subscribe(item.action)
            }

        }
    }

    fun observable(init: V.() -> Observable<T>) {
        observable = init
    }
}

sealed class IItem<T, V : View> {
    open var filter: (Observable<T>.() -> Observable<T>)? = null
    lateinit var view: V
}

class EventItem<T, V : View> : IItem<T, V>() {
    lateinit var event: (T) -> IEvent

    fun event(init: T.() -> IEvent) {
        event = init
    }
}

class ActionItem<T, V : View> : IItem<T, V>() {
    lateinit var action: Consumer<in T>
    fun action(init: (T) -> Unit) {
        action = Consumer { init.invoke(it) }
    }
}

fun <M : IModel> IViewModel<M>.toEvents(init: EventBuilder.() -> Unit): List<Disposable> {
    val builder = EventBuilder()
    builder.init()
    return builder.build(this.action)
}
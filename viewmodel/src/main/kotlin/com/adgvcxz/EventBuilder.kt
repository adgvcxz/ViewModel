package com.adgvcxz

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2018/5/3.
 */
class EventBuilder {
    private val items = mutableListOf<EventItem<Any>>()


    fun build(subject: Subject<IEvent>): List<Disposable> {
        return items.map { item ->
            item.observable()
                .map { item.action.invoke(it) }
                .flatMap {
                    if (it is IEvent) {
                        item.transform?.invoke(Observable.just(it)) ?: Observable.just(it)
                    } else {
                        Observable.empty()
                    }
                }.doOnNext { subject.onNext(it) }.subscribe()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> addItem(init: EventItem<T>.() -> Unit) {
        val item = EventItem<T>()
        item.init()
        items.add(item as EventItem<Any>)
    }
}


class EventItem<T> {
    var transform: (Observable<IEvent>.() -> Observable<IEvent>)? = null

    lateinit var action: (T) -> Any

    lateinit var observable: () -> Observable<T>

    fun observable(init: () -> Observable<T>) {
        observable = init
    }

    fun action(init: T.() -> Any) {
        action = init
    }

    fun transform(init: Observable<IEvent>.() -> Observable<IEvent>) {
        transform = init
    }
}

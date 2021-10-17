package com.adgvcxz

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

@Suppress("UNCHECKED_CAST")
fun <T, R : T> Observable<T>.bindTo(observer: Subject<in R>): Disposable =
    this.subscribe { observer.onNext(it as R) }

fun Disposable.addTo(disposables: CompositeDisposable) {
    disposables.add(this)
}

fun List<Disposable>.addTo(disposables: CompositeDisposable) {
    disposables.addAll(*this.toTypedArray())
}


fun <M, T> ViewModelBuilder<M>.add(
    data: M.() -> T,
    action: T.() -> Unit,
    filter: (Observable<T>.() -> Observable<T>)? = null
) {
    add<T> {
        value { data(this) }
        filter { filter?.invoke(this) ?: this }
        behavior { action.invoke(it) }
    }
}

fun <M : IModel> IViewModel<M>.bindModel(
    disposables: CompositeDisposable,
    init: ViewModelBuilder<M>.() -> Unit
) {
    val builder = ViewModelBuilder<M>()
    builder.init()
    builder.build(this).addTo(disposables)
}

fun <M : IModel> AFViewModel<M>.bindModel(init: ViewModelBuilder<M>.() -> Unit) {
    val builder = ViewModelBuilder<M>()
    builder.init()
    builder.build(this).addTo(disposables)
}

fun <M : IModel> IViewModel<M>.bindEvent(
    disposables: CompositeDisposable,
    init: EventBuilder.() -> Any
) {
    val builder = EventBuilder()
    builder.init()
    builder.build(this.action).addTo(disposables)
}

fun <M : IModel> AFViewModel<M>.bindEvent(
    init: EventBuilder.() -> Any
) {
    val builder = EventBuilder()
    builder.init()
    builder.build(this.action).addTo(disposables)
}

fun <M, T> ViewModelBuilder<M>.add(
    data: M.() -> T,
    action: Consumer<T>,
    filter: (Observable<T>.() -> Observable<T>)? = null
) {
    add<T> {
        value { data(this) }
        filter { filter?.invoke(this) ?: this }
        behavior = action
    }
}

fun <T> EventBuilder.add(
    observable: () -> Observable<T>,
    action: T.() -> Any,
    transform: (Observable<IEvent>.() -> Observable<IEvent>)? = null
) {
    add<T> {
        this.transform = transform
        observable { observable() }
        action { action.invoke(this) }
    }
}



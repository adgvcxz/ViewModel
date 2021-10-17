package com.adgvcxz

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer


/**
 * zhaowei
 * Created by zhaowei on 2018/5/3.
 */
class ViewModelBuilder<M> {

    private val items = arrayListOf<ViewModelItem<M, Any, Any>>()

    fun build(viewModel: IViewModel<M>): List<Disposable> {
        return items.map { item ->
            viewModel.model.map { item.value.invoke(it) }
                .compose { item.filter?.invoke(it) ?: it }
                .flatMap {
                    val value = item.map?.invoke(it) ?: it
                    if (value == null) Observable.empty() else Observable.just(value)
                }.subscribe { item.behavior.accept(it) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <S> add(init: ViewModelItem<M, S, S>.() -> Unit) {
        val item = ViewModelItem<M, S, S>()
        item.init()
        items.add(item as ViewModelItem<M, Any, Any>)
    }

}

class ViewModelItem<M, S, R> {

    lateinit var value: (M.() -> S)
    lateinit var behavior: (Consumer<in R>)
    var filter: (Observable<S>.() -> Observable<S>)? = null
    var map: (S.() -> R)? = null

    fun behavior(init: (R) -> Unit) {
        behavior = Consumer { init.invoke(it) }
    }

    fun value (init: M.() -> S) {
        value = init
    }

    fun map(init: S.() -> R) {
        map = init
    }

    fun filter(init: Observable<S>.() -> Observable<S>) {
        filter = init
    }
}

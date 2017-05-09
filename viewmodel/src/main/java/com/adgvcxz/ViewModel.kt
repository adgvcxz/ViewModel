package com.adgvcxz

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

open class ViewModel<M : IModel>(initModel: M) : IViewModel<M> {

    var action: Subject<IAction> = PublishSubject.create<IAction>().toSerialized()

    var currentModel: M = initModel
        private set

    val model: Observable<M> by lazy {
        this.action
                .flatMap { this.mutate(it) }
                .compose { transform(it) }
                .scan(initModel) { model, mutation -> scan(model, mutation) }
                .retry()
                .share()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { currentModel = it }
                .startWith(currentModel)
    }
}

package com.adgvcxz

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

class ViewModel<S : IState>(initState: S) : IViewModel<S> {

    var action: Subject<IAction> = PublishSubject.create<IAction>().toSerialized()

    var currentState: S = initState
        private set

    val state: Observable<S> = this.action
            .flatMap { this.mutate(it) }
            .compose { transform(it) }
            .scan(initState) { state, mutation -> scan(state, mutation) }
            .retry()
            .share()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { currentState = it }
            .startWith(currentState)
}

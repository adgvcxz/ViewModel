package com.adgvcxz

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

abstract class ViewModel<S : IState>(initState: S) {

    var action: Subject<IAction> = PublishSubject.create<IAction>().toSerialized()

    var currentState: S = initState
    private set

    val state: Observable<S> = this.action
            .flatMap { this.action(it) }
            .scan(initState) { state, mutation -> mutate(state, mutation) }
            .retry()
            .share()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { currentState = it}
            .startWith(currentState)

    abstract fun mutate(state: S, mutation: IMutation): S

    abstract fun action(action: IAction): Observable<IMutation>
}

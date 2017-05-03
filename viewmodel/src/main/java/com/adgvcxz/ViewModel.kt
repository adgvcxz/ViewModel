package com.adgvcxz

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
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
            .flatMap { this.mutate(it) }
            .compose{ transform(it) }
            .scan(initState) { state, mutation -> scan(state, mutation) }
            .retry()
            .share()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { currentState = it }
            .startWith(currentState)

    open fun scan(state: S, mutation: IMutation): S {
        return state
    }

    open fun mutate(action: IAction): Observable<IMutation> {
        return Observable.empty()
    }

    open fun transform(mutation: Observable<IMutation>): Observable<IMutation> {
        return mutation
    }
}

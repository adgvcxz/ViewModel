package com.adgvcxz

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject


/**
 * zhaowei
 * Created by zhaowei on 2017/5/4.
 */

abstract class IViewModel<M> {

    var action: Subject<IEvent> = PublishSubject.create<IEvent>().toSerialized()

    abstract val model: Observable<M>

    open fun scan(model: M, mutation: IMutation): M = model

    open fun mutate(event: IEvent): Observable<IMutation> {
        if (event is IMutation) return Observable.just(event)
        return Observable.empty()
    }

    open fun transformMutation(mutation: Observable<IMutation>): Observable<IMutation> = mutation

    open fun transformEvent(event: Observable<IEvent>): Observable<IEvent> = event


}

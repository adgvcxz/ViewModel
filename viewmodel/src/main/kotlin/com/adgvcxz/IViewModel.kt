package com.adgvcxz

//import android.arch.lifecycle.LifecycleObserver
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2017/5/4.
 */

abstract class IViewModel<M: IModel>/*: LifecycleObserver*/ {

    var action: Subject<IEvent> = PublishSubject.create<IEvent>().toSerialized()

    abstract val model: Observable<M>

    open fun scan(model: M, mutation: IMutation): M = model

    open fun mutate(event: IEvent): Observable<IMutation> = Observable.empty()

    open fun transformMutation(mutation: Observable<IMutation>): Observable<IMutation> = mutation

    open fun transformEvent(event: Observable<IEvent>): Observable<IEvent> = event
}

package com.adgvcxz

//import android.arch.lifecycle.LifecycleObserver
import io.reactivex.Observable

/**
 * zhaowei
 * Created by zhaowei on 2017/5/4.
 */

interface IViewModel<M: IModel>/*: LifecycleObserver*/ {

    fun scan(model: M, mutation: IMutation): M = model

    fun mutate(event: IEvent): Observable<IMutation> = Observable.empty()

    fun transformMutation(mutation: Observable<IMutation>): Observable<IMutation> = mutation

    fun transformEvent(event: Observable<IEvent>): Observable<IEvent> = event
}

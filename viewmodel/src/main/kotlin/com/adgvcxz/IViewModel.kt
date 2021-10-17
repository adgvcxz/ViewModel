package com.adgvcxz

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject


/**
 * zhaowei
 * Created by zhaowei on 2017/5/4.
 */

abstract class IViewModel<M> : LifecycleEventObserver {

    var action: Subject<IEvent> = PublishSubject.create<IEvent>().toSerialized()

    abstract val model: Observable<M>

    open fun scan(model: M, mutation: IMutation): M = model

    open fun mutate(event: IEvent): Observable<IMutation> {
        if (event is IMutation) return Observable.just(event)
        return Observable.empty()
    }

    open fun transformMutation(mutation: Observable<IMutation>): Observable<IMutation> = mutation

    open fun transformEvent(event: Observable<IEvent>): Observable<IEvent> = event

    val disposables: CompositeDisposable = CompositeDisposable()

    fun bind(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_CREATE) {
            model.subscribe().addTo(disposables)
        } else if (event == Lifecycle.Event.ON_DESTROY) {
            disposables.dispose()
        }
        action.onNext(LifecycleEventChanged(source, event))
    }


}

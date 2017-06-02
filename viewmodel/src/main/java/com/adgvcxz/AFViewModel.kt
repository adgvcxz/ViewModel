package com.adgvcxz

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

open class AFViewModel<M : IModel>(initModel: M) : ViewModel(), IViewModel<M> {

    var action: Subject<IEvent> = PublishSubject.create<IEvent>().toSerialized()

    var currentModel: M = initModel
        private set

    val model: Observable<M> by lazy {
        this.action
                .takeUntil(action.filter { it == AFLifecircleEvent.Destroy }.take(1))
                .flatMap { this.mutate(it) }
                .compose { transform(it) }
                .scan(initModel) { model, mutation -> scan(model, mutation) }
                .retry()
                .share()
                .doOnNext { currentModel = it }
                .startWith(currentModel)
                .observeOn(AndroidSchedulers.mainThread())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        this.action.onNext(AFLifecircleEvent.Create)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        this.action.onNext(AFLifecircleEvent.Resume)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        this.action.onNext(AFLifecircleEvent.Start)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        this.action.onNext(AFLifecircleEvent.Pause)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        this.action.onNext(AFLifecircleEvent.Stop)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        this.action.onNext(AFLifecircleEvent.Destroy)
    }
}

package com.adgvcxz

//import android.arch.lifecycle.Lifecycle
//import android.arch.lifecycle.OnLifecycleEvent
//import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

abstract class AFViewModel<M : IModel>: /*: ViewModel(),*/ IViewModel<M> {

    var action: Subject<IEvent> = PublishSubject.create<IEvent>().toSerialized()

    abstract val initModel: M

    var _currentModel: M? = null

    val model: Observable<M> by lazy {
        this.action
                .takeUntil(action.filter { it == AFLifeCircleEvent.Destroy }.take(1))
                .flatMap { this.mutate(it) }
                .compose { transform(it) }
                .scan(initModel) { model, mutation -> scan(model, mutation) }
                .retry()
                .share()
                .startWith(initModel)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { _currentModel = it }
    }

    fun currentModel(): M {
        return _currentModel ?: initModel
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    fun onCreate() {
//        this.action.onNext(AFLifeCircleEvent.Create)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    fun onResume() {
//        this.action.onNext(AFLifeCircleEvent.Resume)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onStart() {
//        this.action.onNext(AFLifeCircleEvent.Start)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//    fun onPause() {
//        this.action.onNext(AFLifeCircleEvent.Pause)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun onStop() {
//        this.action.onNext(AFLifeCircleEvent.Stop)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun onDestroy() {
//        this.action.onNext(AFLifeCircleEvent.Destroy)
//    }
}

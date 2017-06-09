package com.adgvcxz

//import android.arch.lifecycle.Lifecycle
//import android.arch.lifecycle.OnLifecycleEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2017/6/3.
 */
abstract class WidgetViewModel<M : IModel> : IViewModel<M> {

    var action: Subject<IEvent> = PublishSubject.create<IEvent>().toSerialized()

    abstract val initModel: M

    lateinit var currentModel: M

    val model: Observable<M> by lazy {
        currentModel = initModel
        val value = this.action
                .flatMap { this.mutate(it) }
                .compose { transform(it) }
                .scan(currentModel) { model, mutation -> scan(model, mutation) }
                .share()
                .startWith(currentModel)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { currentModel = it }
        value.subscribe()
        value
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    fun onCreate() {
//        this.action.onNext(WidgetLifeCircleEvent.Attach)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun onDestroy() {
//        this.action.onNext(WidgetLifeCircleEvent.Detach)
//    }
}

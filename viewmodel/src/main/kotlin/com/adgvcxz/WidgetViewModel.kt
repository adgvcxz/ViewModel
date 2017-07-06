package com.adgvcxz

//import android.arch.lifecycle.Lifecycle
//import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2017/6/3.
 */
abstract class WidgetViewModel<M : IModel> : IViewModel<M> {

    var action: Subject<IEvent> = PublishSubject.create<IEvent>().toSerialized()

    abstract val initModel: M

    var _currentModel: M? = null

    val model: Observable<M> by lazy {
        this.action
                .doOnSubscribe { _currentModel = initModel }
                .flatMap { this.mutate(it) }
                .compose { transform(it) }
                .scan(initModel) { model, mutation -> scan(model, mutation) }
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
//        this.action.onNext(WidgetLifeCircleEvent.Attach)
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun onDestroy() {
//        this.action.onNext(WidgetLifeCircleEvent.Detach)
//    }
}

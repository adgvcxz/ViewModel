package com.adgvcxz

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

abstract class AFViewModel<M : IModel>: IViewModel<M>(), LifecycleEventObserver {

    abstract val initModel: M

    private var _currentModel: M? = null

    override val model: Observable<M> by lazy {
        this.action
                .compose { transformEvent(it) }
                .flatMap { this.mutate(it) }
                .compose { transformMutation(it) }
                .scan(initModel) { model, mutation -> scan(model, mutation) }
                .doOnError { it.printStackTrace() }
                .retry()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { _currentModel = it }
                .replay(1)
                .refCount()
    }

    fun currentModel(): M = _currentModel ?: initModel

    inline fun <reified T: AFViewModel<M>>bind(lifecycle: Lifecycle): T {
        lifecycle.addObserver(this)
        return this as T
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_CREATE) {
            onCreate()
        } else if (event == Lifecycle.Event.ON_DESTROY) {
            onDestroy()
        }
        action.onNext(LifecycleEventChanged(source, event))
    }


}

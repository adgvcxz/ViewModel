package com.adgvcxz

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

abstract class AFViewModel<M : IModel>: WidgetViewModel<M>(), LifecycleEventObserver {

    val disposables = CompositeDisposable()


    inline fun <reified T: AFViewModel<M>>bind(lifecycle: Lifecycle): T {
        lifecycle.addObserver(this)
        return this as T
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

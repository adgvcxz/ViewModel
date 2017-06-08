package com.adgvcxz

import android.view.View
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

/**
 * zhaowei
 * Created by zhaowei on 2017/6/8.
 */
class ViewAttachesObservable(private val view: View) : Observable<WidgetLifeCircleEvent>() {

    override fun subscribeActual(observer: Observer<in WidgetLifeCircleEvent>) {
        val listener = Listener(view, observer)
        view.addOnAttachStateChangeListener(listener)
    }

    class Listener(private val view: View, private val observer: Observer<in WidgetLifeCircleEvent>) : MainThreadDisposable(), View.OnAttachStateChangeListener {


        override fun onViewDetachedFromWindow(v: View?) {
            observer.onNext(WidgetLifeCircleEvent.Detach)
        }

        override fun onViewAttachedToWindow(v: View?) {
            observer.onNext(WidgetLifeCircleEvent.Attach)
        }

        override fun onDispose() {
            view.removeOnAttachStateChangeListener(this)
        }
    }
}
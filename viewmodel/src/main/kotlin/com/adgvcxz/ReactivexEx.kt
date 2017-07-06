package com.adgvcxz

import android.view.View
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

@Suppress("UNCHECKED_CAST")
fun <T, R : T> Observable<T>.bindTo(observer: Subject<in R>): Disposable = this.subscribe { observer.onNext(it as R) }

fun Disposable.addTo(disposables: CompositeDisposable) {
    disposables.add(this)
}

val View.disposables: CompositeDisposable
    get() = CompositeDisposable()

fun View.aa() {
   this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
       override fun onViewDetachedFromWindow(v: View?) {
           v?.disposables?.dispose()
       }

       override fun onViewAttachedToWindow(v: View?) {
           TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
       }
   })
}
package com.adgvcxz

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.Subject

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

@Suppress("UNCHECKED_CAST")
fun <T, R : T> Observable<T>.bindTo(observer: Subject<in R>): Disposable = this.subscribe { observer.onNext(it as R) }

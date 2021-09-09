package com.adgvcxz.viewmodel.sample

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject


/**
 * zhaowei
 * Created by zhaowei on 2017/7/12.
 */
class RxBus {
    private val bus: Subject<Any> by lazy {
        PublishSubject.create<Any>().toSerialized()
    }

    private object Holder {
        val Instance = RxBus()
    }

    companion object {
        val instance: RxBus by lazy {
            Holder.Instance
        }
    }

    fun post(event: Any) = bus.onNext(event)

    fun <T : Any> toObservable(event: Class<T>): Observable<T> = bus.ofType(event)
}

class ValueChangeEvent(val id: Int, val value: String)
class ViewPagerEvent(val value: String)
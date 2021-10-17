package com.adgvcxz

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

abstract class AFViewModel<M> : IViewModel<M>() {
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
}

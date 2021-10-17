package com.adgvcxz.recyclerviewmodel

import com.adgvcxz.AFViewModel
import com.adgvcxz.IModel
import io.reactivex.rxjava3.disposables.Disposable

/**
 * zhaowei
 * Created by zhaowei on 2017/7/6.
 */
abstract class RecyclerItemViewModel<M : IModel> : AFViewModel<M>() {

    internal var disposable: Disposable? = null

    fun dispose() {
        disposable?.dispose()
    }
}
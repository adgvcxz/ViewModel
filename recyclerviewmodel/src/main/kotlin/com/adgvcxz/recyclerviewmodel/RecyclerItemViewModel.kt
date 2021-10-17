package com.adgvcxz.recyclerviewmodel

import com.adgvcxz.IModel
import com.adgvcxz.WidgetViewModel
import io.reactivex.rxjava3.disposables.Disposable

/**
 * zhaowei
 * Created by zhaowei on 2017/7/6.
 */
abstract class RecyclerItemViewModel<M : IModel> : WidgetViewModel<M>() {

    internal var disposable: Disposable? = null

    fun dispose() {
        disposable?.dispose()
    }
}
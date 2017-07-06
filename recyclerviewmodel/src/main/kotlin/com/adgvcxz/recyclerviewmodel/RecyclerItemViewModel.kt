package com.adgvcxz.recyclerviewmodel

import com.adgvcxz.IModel
import com.adgvcxz.WidgetViewModel
import io.reactivex.disposables.Disposable

/**
 * zhaowei
 * Created by zhaowei on 2017/7/6.
 */
abstract class RecyclerItemViewModel<M : IModel>: WidgetViewModel<M>() {

    var disposable: Disposable? = null

}
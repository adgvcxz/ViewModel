package com.adgvcxz.viewpagermodel

import com.adgvcxz.IModel
import com.adgvcxz.WidgetViewModel
import io.reactivex.disposables.Disposable

/**
 * zhaowei
 * Created by zhaowei on 2017/8/4.
 */
abstract class ViewPagerItemViewModel<M : IModel> : WidgetViewModel<M>() {

    internal lateinit var disposable: Disposable

    internal fun dispose() {
        disposable.dispose()
    }
}
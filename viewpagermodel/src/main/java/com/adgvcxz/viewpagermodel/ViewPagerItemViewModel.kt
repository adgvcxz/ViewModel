package com.adgvcxz.viewpagermodel

import com.adgvcxz.AFViewModel
import com.adgvcxz.IModel
import io.reactivex.rxjava3.disposables.Disposable

/**
 * zhaowei
 * Created by zhaowei on 2017/8/4.
 */
abstract class ViewPagerItemViewModel<M> : AFViewModel<M>() {

    internal lateinit var disposable: Disposable

    internal fun dispose() {
        disposable.dispose()
    }
}
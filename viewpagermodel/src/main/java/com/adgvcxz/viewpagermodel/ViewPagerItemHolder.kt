package com.adgvcxz.viewpagermodel

import android.view.View
import io.reactivex.disposables.CompositeDisposable

/**
 * zhaowei
 * Created by zhaowei on 2017/8/4.
 */
open class ViewPagerItemHolder(val view: View) {
    val disposables: CompositeDisposable by lazy { CompositeDisposable() }
}
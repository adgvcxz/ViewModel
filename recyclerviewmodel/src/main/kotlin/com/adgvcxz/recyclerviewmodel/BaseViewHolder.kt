package com.adgvcxz.recyclerviewmodel

import android.view.View
import io.reactivex.disposables.CompositeDisposable

/**
 * zhaowei
 * Created by zhaowei on 2017/6/19.
 */
open class BaseViewHolder {
    lateinit var itemView: View
    val disposables: CompositeDisposable by lazy { CompositeDisposable() }
}

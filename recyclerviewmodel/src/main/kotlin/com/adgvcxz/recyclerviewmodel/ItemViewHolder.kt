package com.adgvcxz.recyclerviewmodel

import android.support.v7.widget.RecyclerView
import android.view.View
import io.reactivex.disposables.CompositeDisposable

/**
 * zhaowei
 * Created by zhaowei on 2017/6/19.
 */
open class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val disposables: CompositeDisposable = CompositeDisposable()
}

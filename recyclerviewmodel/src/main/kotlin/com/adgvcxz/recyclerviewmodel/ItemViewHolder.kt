package com.adgvcxz.recyclerviewmodel

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * zhaowei
 * Created by zhaowei on 2017/6/19.
 */
open class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val disposables: CompositeDisposable = CompositeDisposable()
}

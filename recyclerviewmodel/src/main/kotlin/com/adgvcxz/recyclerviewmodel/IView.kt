package com.adgvcxz.recyclerviewmodel

import android.view.View
import android.view.ViewGroup
import com.adgvcxz.IModel

/**
 * zhaowei
 * Created by zhaowei on 2017/6/5.
 */
interface IView<V: ItemViewHolder, in M: RecyclerItemViewModel<out IModel>> {

    val layoutId: Int

    @Suppress("UNCHECKED_CAST")
    fun initView(view: View, parent: ViewGroup): V {
        return ItemViewHolder(view) as V
    }

    fun bind(viewHolder: V, viewModel: M, position: Int) {

    }
}
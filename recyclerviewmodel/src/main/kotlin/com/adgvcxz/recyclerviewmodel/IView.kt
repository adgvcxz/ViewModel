package com.adgvcxz.recyclerviewmodel

import android.view.View
import com.adgvcxz.IModel

/**
 * zhaowei
 * Created by zhaowei on 2017/6/5.
 */
interface IView<V: BaseViewHolder, in M: RecyclerItemViewModel<out IModel>> {

    val layoutId: Int

    @Suppress("UNCHECKED_CAST")
    fun initView(view: View): V {
        return BaseViewHolder() as V
    }

    fun bind(viewHolder: V, viewModel: M) {

    }
}
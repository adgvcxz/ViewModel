package com.adgvcxz.recyclerviewmodel

import androidx.recyclerview.widget.DiffUtil
import com.adgvcxz.IModel

/**
 * zhaowei
 * Created by zhaowei on 2017/6/7.
 */
class ItemDiffCallback(private val oldItems: List<RecyclerItemViewModel<out IModel>>,
                       private val newItems: List<RecyclerItemViewModel<out IModel>>) : DiffUtil.Callback() {


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].currentModel() == newItems[newItemPosition].currentModel()

    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition] == newItems[newItemPosition]

}

package com.adgvcxz.viewmodel.sample

import android.util.Log
import com.adgvcxz.IModel
import com.adgvcxz.viewpagermodel.*
import kotlinx.android.synthetic.main.item_view_pager.view.*

/**
 * zhaowei
 * Created by zhaowei on 2017/8/7.
 */

class SimpleViewPagerViewModel: ViewPagerViewModel() {
    override val initModel: ViewPagerModel = ViewPagerModel(arrayListOf(ItemViewModel(), ItemViewModel(), ItemViewModel(), ItemViewModel()))
}

class ItemModel: IModel {
    var value = "abababab"
}

class ItemViewModel: ViewPagerItemViewModel<ItemModel>() {
    override val initModel: ItemModel = ItemModel()
}

class ItemView: IPagerItemView<ViewPagerItemHolder, ItemViewModel> {
    override val layoutId: Int = R.layout.item_view_pager

    override fun bind(holder: ViewPagerItemHolder, viewModel: ItemViewModel, position: Int) {
        Log.e("zhaow", "bind    ${viewModel.currentModel().value}")
        holder.view.textView.text = viewModel.currentModel().value
    }
}
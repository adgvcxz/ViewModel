package com.adgvcxz.viewpagermodel

import com.adgvcxz.IModel

/**
 * zhaowei
 * Created by zhaowei on 2017/8/4.
 */
interface IPagerItemView<in V: ViewPagerItemHolder, in M: ViewPagerItemViewModel<out IModel>> {
    val layoutId: Int

    fun bind(holder: V, viewModel: M, position: Int) {

    }
}
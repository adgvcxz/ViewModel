package com.adgvcxz.recyclerviewmodel

import android.view.View
import com.adgvcxz.IModel
import com.adgvcxz.WidgetViewModel

/**
 * zhaowei
 * Created by zhaowei on 2017/6/5.
 */
interface IView<in V: WidgetViewModel<out IModel>> {

    val layoutId: Int

    fun initView(view: View) {

    }

    fun bind(view: View, viewModel: V) {

    }
}
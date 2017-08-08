package com.adgvcxz.viewpagermodel

import com.adgvcxz.IModel
import com.adgvcxz.WidgetViewModel

/**
 * zhaowei
 * Created by zhaowei on 2017/8/4.
 */

class ViewPagerModel(values: List<ViewPagerItemViewModel<out IModel>>? = null): IModel {
    var items: List<ViewPagerItemViewModel<out IModel>> = arrayListOf()

    init {
        values?.let { items = it }
        items.forEach { it.disposable = it.model.subscribe() }
    }
}

abstract class ViewPagerViewModel: WidgetViewModel<ViewPagerModel>() {

}
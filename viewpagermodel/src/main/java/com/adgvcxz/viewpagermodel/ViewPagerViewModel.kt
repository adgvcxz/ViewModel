package com.adgvcxz.viewpagermodel

import com.adgvcxz.IModel
import com.adgvcxz.WidgetViewModel

/**
 * zhaowei
 * Created by zhaowei on 2017/8/4.
 */

class ViewPagerModel: IModel {
    val items: List<ViewPagerItemViewModel<out IModel>> = arrayListOf()
}

abstract class ViewPagerViewModel: WidgetViewModel<ViewPagerModel>() {

}
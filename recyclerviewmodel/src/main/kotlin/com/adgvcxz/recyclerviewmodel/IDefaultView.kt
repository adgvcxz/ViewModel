package com.adgvcxz.recyclerviewmodel

import com.adgvcxz.IModel
import com.adgvcxz.WidgetViewModel

/**
 * zhaowei
 * Created by zhaowei on 2017/6/19.
 */

interface IDefaultView<in M: WidgetViewModel<out IModel>>: IView<BaseViewHolder, M>
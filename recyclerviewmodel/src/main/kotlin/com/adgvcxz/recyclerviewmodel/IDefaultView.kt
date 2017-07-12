package com.adgvcxz.recyclerviewmodel

import com.adgvcxz.IModel

/**
 * zhaowei
 * Created by zhaowei on 2017/6/19.
 */

interface IDefaultView<in M: RecyclerItemViewModel<out IModel>>: IView<ItemViewHolder, M>
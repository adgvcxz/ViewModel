package com.adgvcxz

/**
 * zhaowei
 * Created by zhaowei on 2017/7/6.
 */

class DefaultModel: IModel

open class AFDefaultViewModel: AFViewModel<DefaultModel>() {

    override val initModel: DefaultModel = DefaultModel()

}

open class WidgetDefaultViewModel: WidgetViewModel<DefaultModel>() {

    override val initModel: DefaultModel = DefaultModel()

}
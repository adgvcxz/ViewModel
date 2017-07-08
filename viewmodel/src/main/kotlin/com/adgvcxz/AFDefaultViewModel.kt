package com.adgvcxz

/**
 * zhaowei
 * Created by zhaowei on 2017/7/6.
 */

class DefaultModel: IModel

open class AFDefaultViewModel: AFViewModel<DefaultModel>() {

    override var initModel: DefaultModel = DefaultModel()

}

open class WidgetDefaultViewModel: WidgetViewModel<DefaultModel>() {

    override var initModel: DefaultModel = DefaultModel()

}
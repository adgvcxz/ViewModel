package com.adgvcxz.viewmodel.sample

import com.adgvcxz.IModel
import com.adgvcxz.WidgetViewModel
import com.adgvcxz.recyclerviewmodel.IView
import com.adgvcxz.recyclerviewmodel.RecyclerViewModel

/**
 * zhaowei
 * Created by zhaowei on 2017/6/6.
 */

class SimpleRecyclerViewModel: RecyclerViewModel()  {


}


class TextItemView: IView<TextItemViewModel> {
    override val layoutId: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

}

class TextItemViewModel: WidgetViewModel<TextItemViewModel.Model>(Model()) {
    class Model: IModel {
        val content = "abcd"
    }
}
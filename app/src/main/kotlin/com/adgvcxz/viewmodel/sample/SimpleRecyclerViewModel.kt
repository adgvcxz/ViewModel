package com.adgvcxz.viewmodel.sample

import android.view.View
import com.adgvcxz.IModel
import com.adgvcxz.WidgetViewModel
import com.adgvcxz.recyclerviewmodel.IView
import com.adgvcxz.recyclerviewmodel.RecyclerViewModel
import com.jakewharton.rxbinding2.widget.text
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_text_view.view.*

/**
 * zhaowei
 * Created by zhaowei on 2017/6/6.
 */

class SimpleRecyclerViewModel : RecyclerViewModel() {

    override val initModel: Model = Model((0 until 10).map { TextItemViewModel() })


    override fun request(refresh: Boolean): Observable<List<WidgetViewModel<out IModel>>> {
        return Observable.just((0 until 10).map { TextItemViewModel() })
    }

}

class TextItemView : IView<TextItemViewModel> {
    override val layoutId: Int = R.layout.item_text_view

    override fun bind(view: View, viewModel: TextItemViewModel) {
        viewModel.model.map { it.content }
                .distinctUntilChanged()
                .subscribe(view.textView.text())
    }

}

class TextItemViewModel : WidgetViewModel<TextItemViewModel.Model>() {

    override val initModel: Model = Model()

    class Model : IModel {
        val content = "abcd"
    }
}
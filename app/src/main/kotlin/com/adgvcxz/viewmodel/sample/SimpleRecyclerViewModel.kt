package com.adgvcxz.viewmodel.sample

import android.view.View
import com.adgvcxz.IModel
import com.adgvcxz.WidgetViewModel
import com.adgvcxz.recyclerviewmodel.IView
import com.adgvcxz.recyclerviewmodel.LoadingItemViewModel
import com.adgvcxz.recyclerviewmodel.RecyclerViewModel
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_loading.view.*
import kotlinx.android.synthetic.main.item_text_view.view.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * zhaowei
 * Created by zhaowei on 2017/6/6.
 */

class SimpleRecyclerViewModel : RecyclerViewModel() {

    override fun initModel(): Model {
        return Model((0 until 2).map { TextItemViewModel() }, LoadingItemViewModel(), false)
    }


    override fun request(refresh: Boolean): Observable<List<WidgetViewModel<out IModel>>> {
        return Observable.timer(1, TimeUnit.SECONDS)
                .map { (0 until 30).map { TextItemViewModel() } }
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
    override fun initModel(): Model {
        return Model()
    }

    class Model : IModel {
        val content = UUID.randomUUID().toString()
    }
}

class LoadingItemView : IView<LoadingItemViewModel> {

    override val layoutId: Int = R.layout.item_loading

    override fun bind(view: View, viewModel: LoadingItemViewModel) {
        viewModel.model.map { it.state }
                .map { it != LoadingItemViewModel.State.failure }
                .subscribe(view.loading.visibility())
        viewModel.model.map { it.state }
                .map { it == LoadingItemViewModel.State.failure }
                .subscribe(view.failed.visibility())
    }
}
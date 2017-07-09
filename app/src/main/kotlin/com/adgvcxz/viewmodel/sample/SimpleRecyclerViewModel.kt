package com.adgvcxz.viewmodel.sample

import android.view.View
import android.widget.TextView
import com.adgvcxz.IModel
import com.adgvcxz.addTo
import com.adgvcxz.bindTo
import com.adgvcxz.recyclerviewmodel.*
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_loading.view.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * zhaowei
 * Created by zhaowei on 2017/6/6.
 */

class SimpleRecyclerViewModel : RecyclerViewModel() {

    override var initModel: RecyclerModel = RecyclerModel(null, true, true)


    override fun request(refresh: Boolean): Observable<ListResult> {
//        if (Random().nextInt() < 16) {
        return Observable.timer(1, TimeUnit.SECONDS)
                .map { (0 until 10).map { TextItemViewModel() } }
                .map { ListResult(it) }
//        } else {
//            return Observable.just(ListResult(null))
//        }
    }
}

class TextItemView : IView<TextItemView.TextItemViewHolder, TextItemViewModel> {
    override val layoutId: Int = R.layout.item_text_view

    class TextItemViewHolder : BaseViewHolder() {
        lateinit var content: TextView
    }


    override fun initView(view: View): TextItemViewHolder {
        return TextItemViewHolder().also {
            it.content = view.findViewById(R.id.textView) as TextView
        }
    }

    override fun bind(viewHolder: TextItemViewHolder, viewModel: TextItemViewModel) {
//        viewHolder.content.text = viewModel.currentModel().content
        viewModel.model.map { it.content }
                .distinctUntilChanged()
                .subscribe(viewHolder.content.text())
                .addTo(viewHolder.disposables)
    }
}

class TextItemViewModel : RecyclerItemViewModel<TextItemViewModel.Model>() {
    override var initModel: Model = Model()

    class Model : IModel {
        val content: String = UUID.randomUUID().toString()
    }
}

class LoadingItemView : IDefaultView<LoadingItemViewModel> {

    override val layoutId: Int = R.layout.item_loading

    override fun bind(viewHolder: BaseViewHolder, viewModel: LoadingItemViewModel) {
        viewModel.model.map { it.state }
                .map { it != LoadingItemViewModel.State.failure }
                .subscribe(viewHolder.itemView.loading.visibility())
                .addTo(viewHolder.disposables)

        viewModel.model.map { it.state }
                .map { it == LoadingItemViewModel.State.failure }
                .subscribe(viewHolder.itemView.failed.visibility())
                .addTo(viewHolder.disposables)
    }
}
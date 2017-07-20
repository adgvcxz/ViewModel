package com.adgvcxz.viewmodel.sample

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import com.adgvcxz.addTo
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

var initId = 0

class SimpleRecyclerViewModel : RecyclerViewModel() {

    override var initModel: RecyclerModel = RecyclerModel(null, true, false)


    override fun request(refresh: Boolean): Observable<IMutation> {
        if (Random().nextInt() < 16) {
            return Observable.timer(1, TimeUnit.SECONDS)
                    .map { (0 until 10).map { TextItemViewModel() } }
                    .flatMap {
                        if (!refresh && currentModel().items.size > 30) {
                            Observable.concat(Observable.just(DataMutation.UpdateData(it)), Observable.just(Mutation.removeLoadingItem))
                        } else {
                            Observable.just(DataMutation.UpdateData(it))
                        }
                    }
        } else {
            return Observable.just(Mutation.LoadFailure)
        }
    }
}

class TextItemView : IView<TextItemView.TextItemViewHolder, TextItemViewModel> {
    override val layoutId: Int = R.layout.item_text_view

    class TextItemViewHolder(view: View) : ItemViewHolder(view) {
        var content: TextView = view.findViewById(R.id.textView) as TextView
    }


    override fun initView(view: View, parent: ViewGroup): TextItemViewHolder {
        return TextItemViewHolder(view)
    }

    override fun bind(viewHolder: TextItemViewHolder, viewModel: TextItemViewModel, position: Int) {
//        viewHolder.content.text = viewModel.currentModel().content
        viewModel.model.map { it.content }
                .distinctUntilChanged()
                .subscribe(viewHolder.content.text())
                .addTo(viewHolder.disposables)
    }
}

class TextItemViewModel : RecyclerItemViewModel<TextItemViewModel.Model>() {
    override var initModel: Model = Model()

    class ValueChangeMutation(val value: String) : IMutation

    override fun transform(mutation: Observable<IMutation>): Observable<IMutation> {
        val value = RxBus.instance.toObservable(ValueChangeEvent::class.java)
                .filter { it.id == currentModel().id }
                .map { ValueChangeMutation(it.value) }
        return Observable.merge(value, mutation)
    }

    override fun scan(model: Model, mutation: IMutation): Model {
        when (mutation) {
            is ValueChangeMutation -> model.content = mutation.value
        }
        return model
    }

    class Model : IModel {
        var content: String = UUID.randomUUID().toString() + "====    $initId"
        var id = initId++
    }
}

class LoadingItemView : IDefaultView<LoadingItemViewModel> {

    override val layoutId: Int = R.layout.item_loading

    override fun bind(viewHolder: ItemViewHolder, viewModel: LoadingItemViewModel, position: Int) {
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
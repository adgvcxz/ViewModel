package com.adgvcxz.viewmodel.sample

import android.view.View
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import com.adgvcxz.add
import com.adgvcxz.bindModel
import com.adgvcxz.recyclerviewmodel.*
import com.adgvcxz.viewmodel.sample.databinding.ItemTextViewBinding
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_loading.view.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * zhaowei
 * Created by zhaowei on 2017/6/6.
 */

var initId = 0

class SimpleRecyclerViewModel : RecyclerViewModel() {

    override var initModel: RecyclerModel =
        RecyclerModel(null, hasLoadingItem = true, isAnim = true)


    override fun request(refresh: Boolean): Observable<IMutation> {
        return if (refresh) {
            Observable.timer(3, TimeUnit.SECONDS)
                .map { (0 until 10).map { TextItemViewModel() } }
                .flatMap {
                    if (!refresh && currentModel().items.size > 30) {
                        Observable.concat(
                            Observable.just(UpdateData(it)),
                            Observable.just(RemoveLoadingItem)
                        )
                    } else {
                        Observable.just(UpdateData(it))
                    }
                }
        } else {
            Observable.timer(100, TimeUnit.MILLISECONDS).map { LoadFailure }
        }
    }
}

class TextItemView : ItemBindingView<ItemTextViewBinding, TextItemViewModel> {
    override val layoutId: Int = R.layout.item_text_view

    override fun generateViewBinding(view: View): ItemTextViewBinding {
        return ItemTextViewBinding.bind(view)
    }

    override fun bind(
        binding: ItemTextViewBinding,
        viewModel: TextItemViewModel,
        position: Int,
        disposable: CompositeDisposable
    ) {
        viewModel.bindModel(disposable) {
            add({ content }, { binding.textView.text = this })
        }
    }
}

class TextItemViewModel : RecyclerItemViewModel<TextItemViewModel.Model>() {
    override var initModel: Model = Model()

    class ValueChangeMutation(val value: String) : IMutation

    override fun transformMutation(mutation: Observable<IMutation>): Observable<IMutation> {
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
        with(viewHolder.itemView) {
            viewModel.bindModel(viewHolder.disposables) {
                add({ state != LoadingItemViewModel.Failure }, {
                    loading.visibility = if (this) View.VISIBLE else View.GONE
                    failed.visibility = if (this) View.GONE else View.VISIBLE
                })
            }
        }
    }
}
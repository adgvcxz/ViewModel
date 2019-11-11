package com.adgvcxz.viewmodel.sample

import com.adgvcxz.*
import com.adgvcxz.viewpagermodel.*
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_view_pager.view.*
import java.util.*

/**
 * zhaowei
 * Created by zhaowei on 2017/8/7.
 */

class SimpleViewPagerViewModel : ViewPagerViewModel() {
    override val initModel: ViewPagerModel = ViewPagerModel(arrayListOf(ItemViewModel(), ItemViewModel(), ItemViewModel(), ItemViewModel()))
}

class ItemModel : IModel {
    var value = "abababab"
}

class SetValueMutation(val value: String) : IMutation

class ItemViewModel : ViewPagerItemViewModel<ItemModel>() {
    override val initModel: ItemModel = ItemModel()

    override fun transformMutation(mutation: Observable<IMutation>): Observable<IMutation> {
        val event = RxBus.instance.toObservable(ViewPagerEvent::class.java)
                .map { it.value }
                .map(::SetValueMutation)
        return Observable.merge(mutation, event)
    }

    override fun scan(model: ItemModel, mutation: IMutation): ItemModel {
        when (mutation) {
            is SetValueMutation -> model.value = mutation.value
        }
        return super.scan(model, mutation)
    }
}

class ItemView : IPagerItemView<ViewPagerItemHolder, ItemViewModel> {
    override val layoutId: Int = R.layout.item_view_pager

    override fun bind(holder: ViewPagerItemHolder, viewModel: ItemViewModel, position: Int) {
        holder.view.run {
            viewModel.toBind(holder.disposables) {
                add({ value }, { textView.text = this })
            }
            viewModel.toEventBind(holder.disposables) {
                addAction({ clicks() }, textView, {
                    RxBus.instance.post(ViewPagerEvent(UUID.randomUUID().toString()))
                })
            }
        }
    }
}
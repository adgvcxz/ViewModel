package com.adgvcxz.viewmodel.sample

import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import com.adgvcxz.addTo
import com.adgvcxz.viewpagermodel.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.text
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_view_pager.view.*
import java.util.*

/**
 * zhaowei
 * Created by zhaowei on 2017/8/7.
 */

class SimpleViewPagerViewModel: ViewPagerViewModel() {
    override val initModel: ViewPagerModel = ViewPagerModel(arrayListOf(ItemViewModel(), ItemViewModel(), ItemViewModel(), ItemViewModel()))
}

class ItemModel: IModel {
    var value = "abababab"
}

class SetValueMutation(val value: String): IMutation

class ItemViewModel: ViewPagerItemViewModel<ItemModel>() {
    override val initModel: ItemModel = ItemModel()

    override fun transformMutation(mutation: Observable<IMutation>): Observable<IMutation> {
        val event = RxBus.instance.toObservable(ViewPagerEvent::class.java)
                .map { it.value }
                .map(::SetValueMutation)
        return Observable.merge(mutation, event)
    }

    override fun scan(model: ItemModel, mutation: IMutation): ItemModel {
        when(mutation) {
            is SetValueMutation -> model.value = mutation.value
        }
        return super.scan(model, mutation)
    }
}

class ItemView: IPagerItemView<ViewPagerItemHolder, ItemViewModel> {
    override val layoutId: Int = R.layout.item_view_pager

    override fun bind(holder: ViewPagerItemHolder, viewModel: ItemViewModel, position: Int) {
        viewModel.model.map { it.value }
                .distinctUntilChanged()
                .subscribe(holder.view.textView.text())
                .addTo(holder.disposables)
        holder.view.textView
                .clicks()
                .doOnNext { RxBus.instance.post(ViewPagerEvent(UUID.randomUUID().toString())) }
                .subscribe()
                .addTo(holder.disposables)
    }
}
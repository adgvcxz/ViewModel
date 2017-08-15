package com.adgvcxz.viewpagermodel

import com.adgvcxz.IEvent
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import com.adgvcxz.WidgetViewModel
import io.reactivex.Observable

/**
 * zhaowei
 * Created by zhaowei on 2017/8/4.
 */

class ViewPagerModel(values: List<ViewPagerItemViewModel<out IModel>>? = null): IModel {
    var items: List<ViewPagerItemViewModel<out IModel>> = arrayListOf()

    init {
        values?.let { items = it }
        items.forEach { it.disposable = it.model.subscribe() }
    }
}

sealed class ViewPagerEventMutation : IEvent, IMutation
data class AppendData(val data: List<ViewPagerItemViewModel<out IModel>>) : ViewPagerEventMutation()
data class ReplaceData(val index: List<Int>, val data: List<ViewPagerItemViewModel<out IModel>>) : ViewPagerEventMutation()
data class RemoveData(val index: List<Int>) : ViewPagerEventMutation()
data class SetData(val data: List<ViewPagerItemViewModel<out IModel>>) : ViewPagerEventMutation()

abstract class ViewPagerViewModel: WidgetViewModel<ViewPagerModel>() {

    override fun mutate(event: IEvent): Observable<IMutation> {
        if (event is IMutation) {
            return Observable.just(event)
        }
        return super.mutate(event)
    }

    override fun scan(model: ViewPagerModel, mutation: IMutation): ViewPagerModel {
        when(mutation) {
            is AppendData -> model.items += mutation.data.also { it.forEach { it.disposable = it.model.subscribe() } }
            is ReplaceData -> {
                model.items = model.items.mapIndexed { index, viewModel ->
                    if (mutation.index.contains(index)) {
                        viewModel.dispose()
                        mutation.data[mutation.index.indexOf(index)].also { it.disposable = it.model.subscribe() }
                    } else {
                        viewModel
                    }
                }
            }
            is RemoveData -> {
                model.items = model.items.filterIndexed { index, viewModel ->
                    val exist = mutation.index.contains(index)
                    if (exist) {
                        viewModel.dispose()
                    }
                    !exist
                }
            }
            is SetData -> {
                model.items.forEach { it.dispose() }
                model.items = mutation.data.also { it.forEach { it.disposable = it.model.subscribe() } }
            }
        }
        return super.scan(model, mutation)
    }

}
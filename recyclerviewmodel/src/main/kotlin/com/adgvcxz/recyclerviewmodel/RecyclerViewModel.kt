package com.adgvcxz.recyclerviewmodel

import com.adgvcxz.IEvent
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import com.adgvcxz.WidgetViewModel
import io.reactivex.Observable

/**
 * zhaowei
 * Created by zhaowei on 2017/6/5.
 */

abstract class RecyclerViewModel : WidgetViewModel<RecyclerViewModel.Model>() {

    class Model(values: List<WidgetViewModel<out IModel>>? = null, isAnim: Boolean = false) : IModel {
        var isRefresh: Boolean = false
        var isLoading: Boolean = false
        var isAnim: Boolean = isAnim
        var items: List<WidgetViewModel<out IModel>> = values ?: arrayListOf()
    }

    enum class Event : IEvent {
        refresh,
        loadMore
    }

    sealed class BooleanEvent(val value: Boolean): IEvent {
        class setAnim(value: Boolean): BooleanEvent(value)
    }

    sealed class StateMutation(val value: Boolean) : IMutation {
        class SetRefresh(value: Boolean) : StateMutation(value)
        class SetLoadMore(value: Boolean) : StateMutation(value)
        class SetAnim(value: Boolean): StateMutation(value)
    }

    sealed class DataMutation(val data: List<WidgetViewModel<out IModel>>) : IMutation {
        class SetData(data: List<WidgetViewModel<out IModel>>) : DataMutation(data)
        class AppendData(data: List<WidgetViewModel<out IModel>>) : DataMutation(data)
    }

    override fun mutate(event: IEvent): Observable<IMutation> {
        when (event) {
            Event.refresh -> {
                val start = Observable.just(StateMutation.SetRefresh(true))
                val end = Observable.just(StateMutation.SetRefresh(false))
                return Observable.concat(start,
                        request(true).map { DataMutation.SetData(it) },
                        end)
            }
            Event.loadMore -> {
                val start = Observable.just(StateMutation.SetLoadMore(true))
                val end = Observable.just(StateMutation.SetLoadMore(false))
                return Observable.concat(start,
                        request(false).map { DataMutation.AppendData(it) },
                        end)
            }
            is BooleanEvent.setAnim -> return Observable.just(StateMutation.SetAnim(event.value))
        }
        return super.mutate(event)
    }

    override fun scan(model: Model, mutation: IMutation): Model {
        when (mutation) {
            is StateMutation.SetRefresh -> model.isRefresh = mutation.value
            is StateMutation.SetLoadMore -> model.isLoading = mutation.value
            is StateMutation.SetAnim -> model.isAnim = mutation.value
            is DataMutation.SetData -> model.items = mutation.data
            is DataMutation.AppendData -> model.items += mutation.data
        }
        return model
    }

    open fun request(refresh: Boolean): Observable<List<WidgetViewModel<out IModel>>> {
        return Observable.empty()
    }

    val count: Int
        get() = currentModel.items.size
}
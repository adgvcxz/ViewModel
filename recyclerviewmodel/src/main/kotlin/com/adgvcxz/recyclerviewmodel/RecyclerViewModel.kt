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

    class Model(values: List<WidgetViewModel<out IModel>>? = null,
                var loadingViewModel: LoadingItemViewModel? = null,
                var isAnim: Boolean = false) : IModel {

        var isRefresh: Boolean = false
        var items: List<WidgetViewModel<out IModel>> = arrayListOf()

        init {
            values?.let { items = values }
            loadingViewModel?.let {
                if (!items.isEmpty()) {
                    items += it
                }
            }
        }
        val isLoading: Boolean
            get() = loadingViewModel?.currentModel?.state == LoadingItemViewModel.State.loading
    }

    enum class Event : IEvent {
        refresh,
        loadMore,
        hideLoadingItem
    }

    sealed class BooleanEvent(val value: Boolean) : IEvent {
        class setAnim(value: Boolean) : BooleanEvent(value)
    }

    sealed class StateMutation(val value: Boolean) : IMutation {
        class SetRefresh(value: Boolean) : StateMutation(value)
        class SetAnim(value: Boolean) : StateMutation(value)
    }

    sealed class DataMutation(val data: List<WidgetViewModel<out IModel>>) : IMutation {
        class SetData(data: List<WidgetViewModel<out IModel>>) : DataMutation(data)
        class AppendData(data: List<WidgetViewModel<out IModel>>) : DataMutation(data)
    }

    enum class Mutation : IMutation {
        removeLoadingItem
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
                currentModel.loadingViewModel?.action?.onNext(
                        LoadingItemViewModel.StateEvent.SetState(LoadingItemViewModel.State.loading))
                return request(false)
                        .doOnNext { currentModel.loadingViewModel?.action?.onNext(
                                LoadingItemViewModel.StateEvent.SetState(LoadingItemViewModel.State.success)) }
                        .map { DataMutation.AppendData(it) }
            }
            Event.hideLoadingItem -> return Observable.just(Mutation.removeLoadingItem)
            is BooleanEvent.setAnim -> return Observable.just(StateMutation.SetAnim(event.value))
        }
        return super.mutate(event)
    }

    override fun scan(model: Model, mutation: IMutation): Model {
        when (mutation) {
            is StateMutation.SetRefresh -> model.isRefresh = mutation.value
            is StateMutation.SetAnim -> model.isAnim = mutation.value
            is DataMutation.SetData -> {
                model.items = mutation.data
                model.loadingViewModel?.let {
                    if (!model.items.isEmpty()) {
                        model.items += it
                    }
                }
            }
            is DataMutation.AppendData -> {
                if (model.items.last() is LoadingItemViewModel) {
                    model.items = model.items.subList(0, model.items.size - 1)
                }
                model.items += mutation.data
                model.loadingViewModel?.let { model.items += it }
            }
            Mutation.removeLoadingItem -> {
                if (model.items.last() is LoadingItemViewModel) {
                    model.items = model.items.subList(0, model.items.size - 1)
                    model.loadingViewModel = null
                }
            }
        }
        return model
    }

    open fun request(refresh: Boolean): Observable<List<WidgetViewModel<out IModel>>> {
        return Observable.empty()
    }

    val count: Int
        get() = currentModel.items.size
}
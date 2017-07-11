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

class RecyclerModel(values: List<RecyclerItemViewModel<out IModel>>? = null,
            hasLoadingItem: Boolean = false,
            var isAnim: Boolean = false) : IModel {

    var isRefresh: Boolean = false
    var items: List<RecyclerItemViewModel<out IModel>> = arrayListOf()
    var loadingViewModel: LoadingItemViewModel? = null

    init {
        values?.let { items = values }
        if (hasLoadingItem) {
            loadingViewModel = LoadingItemViewModel()
        }
        loadingViewModel?.let {
            if (!items.isEmpty()) {
                items += it
            }
        }
        items.forEach { it.disposable = it.model.subscribe() }
    }

    val isLoading: Boolean
        get() {
            val state = loadingViewModel?.currentModel()?.state
            if (state != null) {
                return state == LoadingItemViewModel.State.loading
            }
            return false
        }
}

abstract class RecyclerViewModel : WidgetViewModel<RecyclerModel>() {

    enum class Event : IEvent {
        refresh,
        loadMore,
        hideLoadingItem
    }

    class ReplaceDataEvent(val index: List<Int>, val data: List<RecyclerItemViewModel<out IModel>>) : IEvent
    class SetDataEvent(val data: List<RecyclerItemViewModel<out IModel>>) : IEvent
    class AppendDataEvent(val data: List<RecyclerItemViewModel<out IModel>>) : IEvent
    class RemoveDataEvent(val index: List<Int>) : IEvent

    sealed class BooleanEvent(val value: Boolean) : IEvent {
        class setAnim(value: Boolean) : BooleanEvent(value)
    }

    sealed class StateMutation(val value: Boolean) : IMutation {
        class SetRefresh(value: Boolean) : StateMutation(value)
        class SetAnim(value: Boolean) : StateMutation(value)
    }

    sealed class DataMutation(val data: List<RecyclerItemViewModel<out IModel>>) : IMutation {
        class SetData(data: List<RecyclerItemViewModel<out IModel>>) : DataMutation(data)
        class AppendData(data: List<RecyclerItemViewModel<out IModel>>) : DataMutation(data)
        class ReplaceData(val index: List<Int>, data: List<RecyclerItemViewModel<out IModel>>) : DataMutation(data)
    }

    class RemoveDateMutation(val index: List<Int>) : IMutation

    enum class Mutation : IMutation {
        removeLoadingItem
    }

    override fun mutate(event: IEvent): Observable<IMutation> {
        when (event) {
            Event.refresh -> {
                if (currentModel().isRefresh || currentModel().isLoading) {
                    return Observable.empty()
                }
                val start = Observable.just(StateMutation.SetRefresh(true))
                val end = Observable.just(StateMutation.SetRefresh(false))
                return Observable.concat(start,
                        request(true).flatMap { if (it.value == null) Observable.empty() else Observable.just(it.value) }
                                .map { DataMutation.SetData(it) },
                        end)
            }
            Event.loadMore -> {
                if (currentModel().isRefresh || currentModel().isLoading) {
                    return Observable.empty()
                }
                currentModel().loadingViewModel?.action?.onNext(
                        LoadingItemViewModel.StateEvent.SetState(LoadingItemViewModel.State.loading))
                return request(false)
                        .doOnNext {
                            if (it.value == null) {
                                currentModel().loadingViewModel?.action?.onNext(
                                        LoadingItemViewModel.StateEvent.SetState(LoadingItemViewModel.State.failure))
                            } else {
                                currentModel().loadingViewModel?.action?.onNext(
                                        LoadingItemViewModel.StateEvent.SetState(LoadingItemViewModel.State.success))
                            }
                        }
                        .flatMap { if (it.value == null) Observable.empty() else Observable.just(it.value) }
                        .map { DataMutation.AppendData(it) }

            }
            Event.hideLoadingItem -> return Observable.just(Mutation.removeLoadingItem)
            is BooleanEvent.setAnim -> return Observable.just(StateMutation.SetAnim(event.value))
            is ReplaceDataEvent -> return Observable.just(DataMutation.ReplaceData(event.index, event.data))
            is RemoveDataEvent -> return Observable.just(RemoveDateMutation(event.index))
            is SetDataEvent -> return Observable.just(DataMutation.SetData(event.data))
            is AppendDataEvent -> return Observable.just(DataMutation.AppendData(event.data))
        }
        return super.mutate(event)
    }

    override fun scan(model: RecyclerModel, mutation: IMutation): RecyclerModel {
        when (mutation) {
            is StateMutation.SetRefresh -> {
                model.isRefresh = mutation.value
            }
            is StateMutation.SetAnim -> model.isAnim = mutation.value
            is DataMutation.SetData -> {
                model.items.forEach { it.dispose() }
                model.items = mutation.data
                model.loadingViewModel?.let {
                    if (!model.items.isEmpty()) {
                        model.items += it
                    }
                }
                model.items.forEach { it.disposable = it.model.subscribe() }
            }
            is DataMutation.AppendData -> {
                if (model.items.last() is LoadingItemViewModel) {
                    model.items = model.items.subList(0, model.items.size - 1)
                }
                model.items += mutation.data
                mutation.data.forEach { it.disposable = it.model.subscribe() }
                model.loadingViewModel?.let { model.items += it }
            }
            is DataMutation.ReplaceData -> {
                model.items = model.items.mapIndexed { index, viewModel ->
                    if (mutation.index.contains(index)) {
                        viewModel.dispose()
                        mutation.data[mutation.index.indexOf(index)].also { it.disposable = it.model.subscribe() }
                    } else {
                        viewModel
                    }
                }
            }
            is RemoveDateMutation -> {
                model.items = model.items.filterIndexed { index, viewModel ->
                    val exist = mutation.index.contains(index)
                    if (exist) {
                        viewModel.dispose()
                    }
                    !exist
                }
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

    open fun request(refresh: Boolean): Observable<ListResult> {
        return Observable.empty()
    }

    val count: Int
        get() = currentModel().items.size
}
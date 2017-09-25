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
                    val hasLoadingItem: Boolean = false,
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
                return state == LoadingItemViewModel.State.Loading
            }
            return false
        }
}

sealed class RecyclerViewEvent : IEvent
object Refresh : RecyclerViewEvent()
object LoadMore : RecyclerViewEvent()


sealed class RecyclerViewMutation : IMutation
data class SetRefresh(val value: Boolean) : RecyclerViewMutation()
data class UpdateData(val data: List<RecyclerItemViewModel<out IModel>>) : RecyclerViewMutation()
object LoadFailure : RecyclerViewMutation()


sealed class RecyclerViewEventMutation : IEvent, IMutation
object RemoveLoadingItem : RecyclerViewEventMutation()
data class SetAnim(val value: Boolean) : RecyclerViewEventMutation()
data class AppendData(val data: List<RecyclerItemViewModel<out IModel>>) : RecyclerViewEventMutation()
data class ReplaceData(val index: List<Int>, val data: List<RecyclerItemViewModel<out IModel>>) : RecyclerViewEventMutation()
data class RemoveData(val index: List<Int>) : RecyclerViewEventMutation()
data class SetData(val data: List<RecyclerItemViewModel<out IModel>>) : RecyclerViewEventMutation()


abstract class RecyclerViewModel : WidgetViewModel<RecyclerModel>() {

    override fun mutate(event: IEvent): Observable<IMutation> {
        when (event) {
            is Refresh -> {
                if (currentModel().isRefresh || currentModel().isLoading) {
                    return Observable.empty()
                }
                val start = Observable.just(SetRefresh(true))
                val end = Observable.just(SetRefresh(false))
                return Observable.concat(start,
                        request(true).map {
                            when (it) {
                                is UpdateData -> SetData(it.data)
                                else -> it
                            }
                        },
                        end)
            }
            is LoadMore -> {
                if (currentModel().isRefresh || currentModel().isLoading) {
                    return Observable.empty()
                }
                currentModel().loadingViewModel?.action?.onNext(
                        LoadingItemViewModel.StateEvent.SetState(LoadingItemViewModel.State.Loading))
                return request(false)
                        .map {
                            when (it) {
                                is UpdateData -> AppendData(it.data)
                                else -> it
                            }
                        }
                        .doOnNext {
                            when (it) {
                                is LoadFailure -> currentModel().loadingViewModel?.action?.onNext(
                                        LoadingItemViewModel.StateEvent.SetState(LoadingItemViewModel.State.Failure))
                                is AppendData -> currentModel().loadingViewModel?.action?.onNext(
                                        LoadingItemViewModel.StateEvent.SetState(LoadingItemViewModel.State.Success))
                            }
                        }
            }
            is RecyclerViewEventMutation -> return Observable.just(event)
        }
        return super.mutate(event)
    }

    override fun scan(model: RecyclerModel, mutation: IMutation): RecyclerModel {
        when (mutation) {
            is SetRefresh -> {
                model.isRefresh = mutation.value
            }
            is SetAnim -> model.isAnim = mutation.value
            is SetData -> {
                model.items.forEach { it.dispose() }
                model.items = mutation.data
                if (model.hasLoadingItem) {
                    if (model.loadingViewModel == null) {
                        model.loadingViewModel = LoadingItemViewModel()
                    }
                    model.loadingViewModel?.let {
                        if (model.items.isNotEmpty()) {
                            model.items += it
                        }
                    }
                }
                model.items.forEach { it.disposable = it.model.subscribe() }
            }
            is AppendData -> {
                if (model.items.isNotEmpty() && model.items.last() is LoadingItemViewModel) {
                    model.items = model.items.subList(0, model.items.size - 1)
                }
                model.items += mutation.data
                mutation.data.forEach { it.disposable = it.model.subscribe() }
                model.loadingViewModel?.let { model.items += it }
            }
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
            is RemoveLoadingItem -> {
                if (model.items.isNotEmpty() && model.items.last() is LoadingItemViewModel) {
                    model.items = model.items.subList(0, model.items.size - 1)
                    model.loadingViewModel?.dispose()
                    model.loadingViewModel = null
                }
            }
        }
        return model
    }

    open fun request(refresh: Boolean): Observable<IMutation> = Observable.empty()

    val count: Int
        get() = currentModel().items.size
}
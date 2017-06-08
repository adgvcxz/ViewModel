package com.adgvcxz.recyclerviewmodel

import android.util.Log
import com.adgvcxz.*
import io.reactivex.Observable

/**
 * zhaowei
 * Created by zhaowei on 2017/6/8.
 */
class LoadingItemViewModel : WidgetViewModel<LoadingItemViewModel.Model>() {

    override fun initModel(): Model = Model()

    enum class State {
        success,
        failure,
        loading
    }

    class Model : IModel {
        var state: State = State.success
        var isAttach: Boolean = false
    }

    sealed class StateEvent(val state: State) : IEvent {
        class SetState(state: State) : StateEvent(state)
    }

    sealed class StateMutation(val state: State) : IMutation {
        class SetState(state: State) : StateMutation(state)
    }

    override fun mutate(event: IEvent): Observable<IMutation> {
        when (event) {
            is StateEvent.SetState -> return Observable.just(StateMutation.SetState(event.state))
        }
        return super.mutate(event)
    }

    override fun scan(model: Model, mutation: IMutation): Model {
        when (mutation) {
            is StateMutation.SetState -> model.state = mutation.state
        }
        return model
    }

}
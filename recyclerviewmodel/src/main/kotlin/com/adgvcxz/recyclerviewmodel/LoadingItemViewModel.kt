package com.adgvcxz.recyclerviewmodel

import com.adgvcxz.IEvent
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import io.reactivex.Observable

/**
 * zhaowei
 * Created by zhaowei on 2017/6/8.
 */
class LoadingItemViewModel : RecyclerItemViewModel<LoadingItemViewModel.Model>() {


    override var initModel: Model = Model()

    companion object {
        const val Success = 1
        const val Failure = 2
        const val Loading = 3
    }

    class Model : IModel {
        var state: Int = Success
    }

    sealed class StateEvent(val state: Int) : IEvent {
        class SetState(state: Int) : StateEvent(state)
    }

    sealed class StateMutation(val state: Int) : IMutation {
        class SetState(state: Int) : StateMutation(state)
    }

    override fun mutate(event: IEvent): Observable<IMutation> {
        when (event) {
            is StateEvent.SetState -> return Observable.just(StateMutation.SetState(event.state))
        }
        return super.mutate(event)
    }

    override fun scan(model: Model, mutation: IMutation): Model {
        when (mutation) {
            is StateMutation.SetState -> {
                model.state = mutation.state
            }
        }
        return model
    }

}
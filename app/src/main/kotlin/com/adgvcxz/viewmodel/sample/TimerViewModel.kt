package com.adgvcxz.viewmodel.sample

import com.adgvcxz.AFViewModel
import com.adgvcxz.IEvent
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

class TimerViewModel : AFViewModel<TimerViewModel.Model>() {

    enum class Event : IEvent {
        StartButtonClicked,
        StopButtonClicked,
    }

    enum class Mutation : IMutation {
        StartTimer,
        StopTimer,
        PauseTimer,
        Timing
    }

    enum class TimerStatus {
        Completed,
        Timing,
        Pause,
    }

    class Model : IModel {
        var time: Int = 0
        var status: TimerStatus = TimerStatus.Completed
    }

    override fun mutate(event: IEvent): Observable<IMutation> {
        when (event) {
            Event.StartButtonClicked -> {
                if (this.currentModel().status == TimerStatus.Completed) {
                    val startTimer = Observable.just(Mutation.StartTimer)
                    val timing = Observable.interval(1, 1, TimeUnit.SECONDS)
                        .map { Mutation.Timing }
                        .takeWhile { this.currentModel().status == TimerStatus.Timing }
                    return Observable.concat(startTimer, timing)
                }
            }

            Event.StopButtonClicked -> return Observable.just(Mutation.StopTimer)
                .filter { this.currentModel().status == TimerStatus.Timing }
                .map { it }
        }
        return Observable.empty()
    }

    override fun scan(model: Model, mutation: IMutation): Model {
        when (mutation) {
            Mutation.StartTimer -> {
                model.time = 0
                model.status = TimerStatus.Timing
            }
            Mutation.Timing -> {
                model.status = TimerStatus.Timing
                model.time += 1
            }
            Mutation.PauseTimer -> model.status = TimerStatus.Pause
            Mutation.StopTimer -> model.status = TimerStatus.Completed
        }
        return model
    }

    override var initModel: Model = Model()
}



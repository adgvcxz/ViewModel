package com.adgvcxz.viewmodel.sample

import com.adgvcxz.*
import io.reactivex.Observable
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
        completed,
        timing,
        pause,
    }

    class Model : IModel {
        var time: Int = 0
        var status: TimerViewModel.TimerStatus = TimerViewModel.TimerStatus.completed
    }

    override fun mutate(event: IEvent): Observable<IMutation> {
        when (event) {
            Event.StartButtonClicked -> {
                if (this.currentModel().status == TimerStatus.completed) {
                    val startTimer = Observable.just(Mutation.StartTimer)
                    val timing = Observable.interval(1, 1, TimeUnit.SECONDS)
                            .filter { this.currentModel().status == TimerStatus.timing }
                            .map { Mutation.Timing }
                            .takeWhile { this.currentModel().status == TimerStatus.timing }
                    return Observable.concat(startTimer, timing)
                }
            }

            Event.StopButtonClicked -> return Observable.just(Mutation.StopTimer)
                    .filter { this.currentModel().status == TimerStatus.timing }
                    .map { it }

            AFLifeCircleEvent.Pause -> return Observable.just(Mutation.PauseTimer)
                    .filter { this.currentModel().status == TimerStatus.timing }
                    .map { it }

            AFLifeCircleEvent.Resume -> return Observable.just(Mutation.Timing)
                    .filter { this.currentModel().status == TimerStatus.pause }
                    .map { it }
        }
        return Observable.empty()
    }

    override fun scan(model: Model, mutation: IMutation): Model {
        when (mutation) {
            Mutation.StartTimer -> return model.also {
                it.time = 0
                it.status = TimerStatus.timing
            }
            Mutation.Timing -> return model.also {
                it.status = TimerStatus.timing
                it.time += 1
            }
            Mutation.PauseTimer -> return model.also { it.status = TimerStatus.pause }
            Mutation.StopTimer -> return model.also { it.status = TimerStatus.completed }
        }
        return model
    }

    override val initModel: Model = Model()
}



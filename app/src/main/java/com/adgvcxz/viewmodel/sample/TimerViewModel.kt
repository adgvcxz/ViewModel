package com.adgvcxz.viewmodel.sample

import com.adgvcxz.IAction
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import com.adgvcxz.ViewModel
import com.adgvcxz.adgdo.then
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

class TimerViewModel : ViewModel<TimerViewModel.Model>(Model()) {

    enum class Action : IAction {
        StartButtonClicked,
        StopButtonClicked,
        ActivityPause,
        ActivityResume;
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

    override fun mutate(action: IAction): Observable<IMutation> {
        when (action) {
            Action.StartButtonClicked -> {
                if (this.currentModel.status == TimerStatus.completed) {
                    val startTimer = Observable.just(Mutation.StartTimer)
                    val timing = Observable.interval(1, 1, TimeUnit.SECONDS)
                            .filter { this.currentModel.status == TimerStatus.timing }
                            .map { Mutation.Timing }
                            .takeWhile { this.currentModel.status == TimerStatus.timing }
                    return Observable.concat(startTimer, timing)
                }
            }

            Action.StopButtonClicked -> return Observable.just(Mutation.StopTimer)
                    .filter { this.currentModel.status == TimerStatus.timing }
                    .map { it }

            Action.ActivityPause -> return Observable.just(Mutation.PauseTimer)
                    .filter { this.currentModel.status == TimerStatus.timing }
                    .map { it }

            Action.ActivityResume -> return Observable.just(Mutation.Timing)
                    .filter { this.currentModel.status == TimerStatus.pause }
                    .map { it }
        }
        return Observable.empty()
    }

    override fun scan(model: Model, mutation: IMutation): Model {
        when (mutation) {
            Mutation.StartTimer -> return model.then {
                it.time = 0
                it.status = TimerStatus.timing
            }
            Mutation.Timing -> return model.then {
                it.status = TimerStatus.timing
                it.time++
            }
            Mutation.PauseTimer -> return model.then { it.status = TimerStatus.pause }
            Mutation.StopTimer -> return model.then { it.status = TimerStatus.completed }
        }
        return model
    }
}



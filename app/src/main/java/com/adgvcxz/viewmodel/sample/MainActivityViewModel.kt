package com.adgvcxz.viewmodel.sample

import com.adgvcxz.IMutation
import com.adgvcxz.IState
import com.adgvcxz.ViewModel
import com.adgvcxz.IAction
import com.adgvcxz.adgdo.then
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

class MainActivityViewModel : ViewModel<MainActivityViewModel.State>(State()) {


    override fun action(action: com.adgvcxz.IAction): Observable<IMutation> {
        when (action as IAction) {
            Action.StartButtonClicked -> {
                if (this.currentState.status == TimerStatus.completed) {
                    val startTimer = Observable.just(Mutation.StartTimer)
                    val timing = Observable.interval(1, 1, TimeUnit.SECONDS)
                            .filter { this.currentState.status == TimerStatus.timing }
                            .map { Mutation.Timing }
                            .takeWhile { this.currentState.status == TimerStatus.timing }
                    return Observable.concat(startTimer, timing)
                }
            }

            Action.StopButtonClicked -> return Observable.just(Mutation.StopTimer)
                    .filter { this.currentState.status == TimerStatus.timing }
                    .map { it }

            Action.ActivityPause -> return Observable.just(Mutation.PauseTimer)
                    .filter { this.currentState.status == TimerStatus.timing }
                    .map { it }

            Action.ActivityResume -> return Observable.just(Mutation.Timing)
                    .filter { this.currentState.status == TimerStatus.pause }
                    .map { it }
        }
        return Observable.empty()
    }

    override fun mutate(state: State, mutation: IMutation): State {
        when (mutation as Mutation) {
            Mutation.StartTimer -> return state.then {
                it.time = 0
                it.status = TimerStatus.timing
            }
            Mutation.Timing -> return state.then {
                it.status = TimerStatus.timing
                it.time++
            }
            Mutation.PauseTimer -> return state.then { it.status = TimerStatus.pause }
            Mutation.StopTimer -> return state.then { it.status = TimerStatus.completed }
        }
    }

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

    class State : IState {
        var time: Int = 0
        var status: MainActivityViewModel.TimerStatus = MainActivityViewModel.TimerStatus.completed
    }
}



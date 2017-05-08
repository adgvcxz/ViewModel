package com.adgvcxz.viewmodel.sample

import com.adgvcxz.ViewModel
import com.adgvcxz.bindTo
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.text
import kotlinx.android.synthetic.main.activity_timer.*

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

class TimerActivity : BaseActivity<TimerViewModel.Model>() {

    var viewModel: ViewModel<TimerViewModel.Model> = TimerViewModel()

    override val layoutId: Int = R.layout.activity_timer

    override fun initBinding() {

        start.clicks()
                .map { TimerViewModel.Action.StartButtonClicked }
                .bindTo(viewModel.action)

        stop.clicks()
                .map { TimerViewModel.Action.StopButtonClicked }
                .bindTo(viewModel.action)

        lifeCycle.filter { it == ActivityLifeCircle.Pause }
                .map { TimerViewModel.Action.ActivityPause }
                .bindTo(viewModel.action)

        lifeCycle.filter { it == ActivityLifeCircle.Resume }
                .map { TimerViewModel.Action.ActivityResume }
                .bindTo(viewModel.action)

        viewModel.state
                .filter { it.status == TimerViewModel.TimerStatus.completed }
                .map { "Timer" }
                .subscribe(time.text())

        viewModel.state.filter { it.status == TimerViewModel.TimerStatus.timing }
                .map { it.time }
                .distinctUntilChanged()
                .map { "$it" }
                .subscribe(time.text())
    }
}

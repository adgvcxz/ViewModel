package com.adgvcxz.viewmodel.sample

import android.arch.lifecycle.ViewModelProviders
import com.adgvcxz.bindTo
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.text
import kotlinx.android.synthetic.main.activity_timer.*

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

class TimerActivity : BaseActivity() {

    val viewModel: TimerViewModel by lazy {
        ViewModelProviders.of(this).get(TimerViewModel::class.java)
    }

    override val layoutId: Int = R.layout.activity_timer

    override fun initBinding() {

        start.clicks()
                .map { TimerViewModel.Event.StartButtonClicked }
                .bindTo(viewModel.action)

        stop.clicks()
                .map { TimerViewModel.Event.StopButtonClicked }
                .bindTo(viewModel.action)


        viewModel.model
                .filter { it.status == TimerViewModel.TimerStatus.completed }
                .map { "Timer" }
                .subscribe(time.text())

        viewModel.model.filter { it.status == TimerViewModel.TimerStatus.timing }
                .map { it.time }
                .distinctUntilChanged()
                .map { "$it" }
                .subscribe(time.text())
    }
}

package com.adgvcxz.viewmodel.sample

import com.adgvcxz.add
import com.adgvcxz.bindEvent
import com.adgvcxz.bindModel
import com.jakewharton.rxbinding4.view.clicks
import kotlinx.android.synthetic.main.activity_timer.*

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

class TimerActivity : BaseActivity<TimerViewModel, TimerViewModel.Model>() {

    override val layoutId: Int = R.layout.activity_timer

    override val viewModel: TimerViewModel = TimerViewModel()

    override fun initBinding() {

        viewModel.bindEvent {
            add({ stop.clicks() }, { TimerViewModel.Event.StopButtonClicked })
            add({ start.clicks() }, { TimerViewModel.Event.StartButtonClicked })
        }

        viewModel.bindModel {
            add(
                { status },
                { time.text = "Timer" }) { filter { it == TimerViewModel.TimerStatus.Completed } }
            add(
                { time },
                {
                    time.text = toString()
                }) { filter { viewModel.currentModel().status == TimerViewModel.TimerStatus.Timing } }
        }

    }

}

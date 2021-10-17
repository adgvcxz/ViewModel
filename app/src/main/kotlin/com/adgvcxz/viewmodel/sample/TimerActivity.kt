package com.adgvcxz.viewmodel.sample

//import android.arch.lifecycle.ViewModelProviders
import com.adgvcxz.add
import com.adgvcxz.toBind
import com.adgvcxz.toEventBind
import com.jakewharton.rxbinding4.view.clicks
import kotlinx.android.synthetic.main.activity_timer.*

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

class TimerActivity : BaseActivity() {

    private val viewModel: TimerViewModel by lazy {
        TimerViewModel().bind(lifecycle)
    }

    override val layoutId: Int = R.layout.activity_timer

    override fun initBinding() {

        viewModel.toEventBind {
            add({ stop.clicks() }, { TimerViewModel.Event.StopButtonClicked })
            add({ start.clicks() }, { TimerViewModel.Event.StartButtonClicked })
        }

        viewModel.toBind {
            add({ status }, { time.text = "Timer" }) { filter { it == TimerViewModel.TimerStatus.Completed } }
            add(
                { time },
                {
                    time.text = toString()
                }) { filter { viewModel.currentModel().status == TimerViewModel.TimerStatus.Timing } }
        }

    }
}

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

//    val viewModel: TimerViewModel by lazy {
//        ViewModelProviders.of(this).get(TimerViewModel::class.java)
//    }

    private val viewModel: TimerViewModel by lazy {
        TimerViewModel()
    }

    override val layoutId: Int = R.layout.activity_timer

    override fun initBinding() {

//        lifecycle.addObserver(viewModel)

        viewModel.toEventBind(disposables) {
            add({ clicks() }, start, { TimerViewModel.Event.StartButtonClicked })
            add({ clicks() }, stop, { TimerViewModel.Event.StopButtonClicked })
        }

        viewModel.toBind(disposables) {
            add({ status }, { time.text = "Timer" }) { filter { it == TimerViewModel.TimerStatus.Completed } }
            add(
                { time },
                {
                    time.text = toString()
                }) { filter { viewModel.currentModel().status == TimerViewModel.TimerStatus.Timing } }
        }

    }
}

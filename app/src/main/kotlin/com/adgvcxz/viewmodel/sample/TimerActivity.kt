package com.adgvcxz.viewmodel.sample

//import android.arch.lifecycle.ViewModelProviders
import com.adgvcxz.AFLifeCircleEvent
import com.adgvcxz.addTo
import com.adgvcxz.bindTo
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.text
import kotlinx.android.synthetic.main.activity_timer.*

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

class TimerActivity : BaseActivity() {

//    val viewModel: TimerViewModel by lazy {
//        ViewModelProviders.of(this).get(TimerViewModel::class.java)
//    }

    val viewModel: TimerViewModel by lazy {
        TimerViewModel()
    }

    override val layoutId: Int = R.layout.activity_timer

    override fun initBinding() {

//        lifecycle.addObserver(viewModel)
//
        start.clicks()
                .map { TimerViewModel.Event.StartButtonClicked }
                .bindTo(viewModel.action)
                .addTo(disposables)

        stop.clicks()
                .map { TimerViewModel.Event.StopButtonClicked }
                .bindTo(viewModel.action)
                .addTo(disposables)


        viewModel.model
                .filter { it.status == TimerViewModel.TimerStatus.completed }
                .map { "Timer" }
                .subscribe(time.text())
                .addTo(disposables)

        viewModel.model.filter { it.status == TimerViewModel.TimerStatus.timing }
                .map { it.time }
                .distinctUntilChanged()
                .map { "$it" }
                .subscribe(time.text())
                .addTo(disposables)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.action.onNext(AFLifeCircleEvent.Destroy)
    }
}

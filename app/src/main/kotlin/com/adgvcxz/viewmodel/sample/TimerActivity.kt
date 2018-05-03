package com.adgvcxz.viewmodel.sample

//import android.arch.lifecycle.ViewModelProviders
import android.view.View
import com.adgvcxz.AFLifeCircleEvent
import com.adgvcxz.addTo
import com.adgvcxz.toBuilder
import com.adgvcxz.toEvents
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

    private val viewModel: TimerViewModel by lazy {
        TimerViewModel()
    }

    override val layoutId: Int = R.layout.activity_timer

    override fun initBinding() {

//        lifecycle.addObserver(viewModel)

        viewModel.toEvents {
            section<Unit, View> {
                observable = { clicks() }
                item {
                    event { TimerViewModel.Event.StartButtonClicked }
                    view = start
                }
                item {
                    event { TimerViewModel.Event.StopButtonClicked }
                    view = stop
                }
            }
        }.addTo(disposables)

        viewModel.toBuilder {
            section<TimerViewModel.Model> {
                mapItem<String> {
                    value { this }
                    filter {
                        filter {
                            it.status == TimerViewModel.TimerStatus.Completed
                        }
                    }
                    map { "Timer" }
                    behavior = time.text()
                }
                mapItem<String> {
                    value { this }
                    filter { filter { it.status == TimerViewModel.TimerStatus.Timing } }
                    map { "$time" }
                    behavior = time.text()
                }
            }
        }.addTo(disposables)

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.action.onNext(AFLifeCircleEvent.Destroy)
    }
}

package com.adgvcxz.viewmodel.sample

import com.adgvcxz.AdgState
import com.adgvcxz.AdgViewModel
import com.adgvcxz.bindTo
import com.adgvcxz.viewmodel.sample.databinding.ActivityMainBinding
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.text

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

class MainActivity : BaseActivity<ActivityMainBinding, MainActivityState>() {


    override var viewModel: AdgViewModel<MainActivityState> = MainActivityViewModel()

    init {
        viewModel.state.subscribe()
    }

    override fun initBinding() {

        binding.start.clicks()
                .map { MainActivityViewModel.Action.StartButtonClicked }
                .bindTo(viewModel.action)
        binding.stop.clicks()
                .map { MainActivityViewModel.Action.StopButtonClicked }
                .bindTo(viewModel.action)

        lifeCycle.filter { it == ActivityLifeCircle.Pause }
                .map { MainActivityViewModel.Action.ActivityPause }
                .bindTo(viewModel.action)

        lifeCycle.filter { it == ActivityLifeCircle.Resume }
                .map { MainActivityViewModel.Action.ActivityResume }
                .bindTo(viewModel.action)

        viewModel.state
                .filter { it.status == MainActivityViewModel.TimerStatus.completed }
                .map { "Timer" }
                .subscribe(binding.time.text())

        viewModel.state.filter { it.status == MainActivityViewModel.TimerStatus.timing }
                .map { it.time }
                .distinctUntilChanged()
                .map { "$it" }
                .subscribe(binding.time.text())

    }

    override fun contentId(): Int = R.layout.activity_main

    override fun initState(): AdgState = MainActivityState()
}
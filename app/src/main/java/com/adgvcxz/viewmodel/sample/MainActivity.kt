package com.adgvcxz.viewmodel.sample

import android.content.Intent
import com.adgvcxz.ViewModel
import com.adgvcxz.viewmodel.sample.databinding.ActivityMainBinding
import com.jakewharton.rxbinding2.view.clicks
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel.State>() {

    override var viewModel: ViewModel<MainViewModel.State> = MainViewModel()

    override val layoutId: Int = R.layout.activity_main

    override fun initBinding() {
        super.initBinding()
        binding.timer.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe {
                    startActivity(Intent(this, TimerActivity::class.java))
                }
    }
}

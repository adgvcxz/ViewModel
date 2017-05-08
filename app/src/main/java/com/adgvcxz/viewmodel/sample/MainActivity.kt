package com.adgvcxz.viewmodel.sample

import android.content.Intent
import com.adgvcxz.ViewModel
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity<MainViewModel.Model>() {

    var viewModel: ViewModel<MainViewModel.Model> = MainViewModel()

    override val layoutId: Int = R.layout.activity_main

    override fun initBinding() {
        timer.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe { startActivity(Intent(this, TimerActivity::class.java)) }
    }
}

package com.adgvcxz.viewmodel.sample

import android.content.Intent
import com.adgvcxz.addTo
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    override val layoutId: Int = R.layout.activity_main

    override fun initBinding() {

        timer.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe { startActivity(Intent(this, TimerActivity::class.java)) }
                .addTo(disposables)

        recycler.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe { startActivity(Intent(this, SimpleRecyclerActivity::class.java)) }
                .addTo(disposables)

        rxbus.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe { startActivity(Intent(this, TestActivity::class.java)) }
                .addTo(disposables)

        viewPager.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe { startActivity(Intent(this, SimpleViewPagerActivity::class.java)) }
                .addTo(disposables)
    }
}

package com.adgvcxz.viewmodel.sample

import android.content.Intent
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_simple_recycler.*
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    override val layoutId: Int = R.layout.activity_main

    override fun initBinding() {

        timer.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe { startActivity(Intent(this, TimerActivity::class.java)) }
        test.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe { startActivity(Intent(this, TestActivity::class.java)) }

        recycler.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe { startActivity(Intent(this, SimpleRecyclerActivity::class.java)) }

    }
}

package com.adgvcxz.viewmodel.sample

import android.arch.lifecycle.ViewModelProviders
import com.adgvcxz.bindTo
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.activity_test.*

/**
 * zhaowei
 * Created by zhaowei on 2017/5/9.
 */

class TestActivity : BaseActivity() {


    override val layoutId: Int = R.layout.activity_test

    val viewModel: TestViewModel by lazy {
        ViewModelProviders.of(this).get(TestViewModel::class.java)
    }

    override fun initBinding() {
        button.clicks()
                .map { TestViewModel.Event.StartButtonDidClicked }
                .bindTo(viewModel.action)
    }
}
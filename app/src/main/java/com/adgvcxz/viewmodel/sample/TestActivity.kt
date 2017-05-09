package com.adgvcxz.viewmodel.sample

import android.util.Log
import com.adgvcxz.ViewModel
import com.adgvcxz.bindTo
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_test.*

/**
 * zhaowei
 * Created by zhaowei on 2017/5/9.
 */

class TestActivity: BaseActivity<TestActivityModel.Model>() {

    override val layoutId: Int = R.layout.activity_test

    override val viewModel: ViewModel<TestActivityModel.Model> = TestActivityModel()

    lateinit var disposable: Disposable

    override fun initBinding() {
        button.clicks().map { TestActivityModel.Action.StartButtonDidClicked }
                .bindTo(viewModel.action)

        disposable = viewModel.model.subscribe {
            Log.e("zhaow", "onNext$it")
            (1..10000).forEach {
                val i = it * it
            }
        }

//        disposable = viewModel.model.subscribe {
//            Log.e("zhaow", "onNext======")
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}

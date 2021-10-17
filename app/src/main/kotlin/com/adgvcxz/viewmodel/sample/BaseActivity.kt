package com.adgvcxz.viewmodel.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adgvcxz.IViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

abstract class BaseActivity<T : IViewModel<M>, M> : AppCompatActivity() {

    abstract val layoutId: Int

    abstract val viewModel: T

    val disposables: CompositeDisposable get() = viewModel.disposables

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        viewModel.bind(lifecycle)
        initBinding()
    }

    open fun initBinding() {

    }
}

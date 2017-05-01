package com.adgvcxz.viewmodel.sample

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.adgvcxz.AdgState
import com.adgvcxz.AdgViewModel
import io.reactivex.subjects.PublishSubject

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

abstract class BaseActivity<out B : ViewDataBinding, S : AdgState> : AppCompatActivity() {

    val lifeCycle: PublishSubject<ActivityLifeCircle> = PublishSubject.create<ActivityLifeCircle>()


    val binding: B by lazy {
        DataBindingUtil.setContentView<B>(this, contentId())
    }

    abstract var viewModel: AdgViewModel<S>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        lifeCycle.onNext(ActivityLifeCircle.Create)
    }

    override fun onStart() {
        super.onStart()
        lifeCycle.onNext(ActivityLifeCircle.Start)
    }

    override fun onResume() {
        super.onResume()
        lifeCycle.onNext(ActivityLifeCircle.Resume)
    }

    override fun onPause() {
        super.onPause()
        lifeCycle.onNext(ActivityLifeCircle.Pause)
    }

    override fun onStop() {
        super.onStop()
        lifeCycle.onNext(ActivityLifeCircle.Start)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifeCycle.onNext(ActivityLifeCircle.Destroy)
    }

    override fun onRestart() {
        super.onRestart()
        lifeCycle.onNext(ActivityLifeCircle.ReStart)
    }

    abstract fun contentId(): Int

    abstract fun initState(): AdgState

    open fun initBinding() {

    }
}

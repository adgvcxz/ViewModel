package com.adgvcxz.viewmodel.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.adgvcxz.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

abstract class BaseActivity<S : IModel> : AppCompatActivity() {

    val lifeCycle: PublishSubject<ActivityLifeCircle> = PublishSubject.create<ActivityLifeCircle>()

    abstract val layoutId: Int

    abstract val viewModel: ViewModel<S>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        lifeCycle.filter { it == ActivityLifeCircle.Destroy }
                .map { IAction.dispose }
                .bindTo(viewModel.action)

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

    open fun initBinding() {

    }
}

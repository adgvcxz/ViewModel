package com.adgvcxz.viewmodel.sample

import android.content.Intent
import com.adgvcxz.AFViewModel
import com.adgvcxz.addTo
import com.jakewharton.rxbinding4.view.clicks
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class EmptyModel
class EmptyViewModel : AFViewModel<EmptyModel>() {
    override val initModel: EmptyModel = EmptyModel()

}

abstract class EmptyActivity : BaseActivity<EmptyViewModel, EmptyModel>() {
    override val viewModel: EmptyViewModel = EmptyViewModel()
}

class MainActivity : EmptyActivity() {

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

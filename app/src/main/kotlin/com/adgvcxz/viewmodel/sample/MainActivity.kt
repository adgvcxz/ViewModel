package com.adgvcxz.viewmodel.sample

import android.content.Intent
import com.adgvcxz.*
import com.jakewharton.rxbinding4.view.clicks
import kotlinx.android.synthetic.main.activity_main.*

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

        viewModel.bindEvent {
            add(
                timer.clicks(),
                { startActivity(Intent(this@MainActivity, TimerActivity::class.java)) })
            add(
                recycler.clicks(),
                { startActivity(Intent(this@MainActivity, SimpleRecyclerActivity::class.java)) })
            add(
                rxbus.clicks(),
                { startActivity(Intent(this@MainActivity, TestActivity::class.java)) })
            add(
                viewPager.clicks(),
                { startActivity(Intent(this@MainActivity, SimpleViewPagerActivity::class.java)) })
        }
    }
}

package com.adgvcxz.viewmodel.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.adgvcxz.addTo
import com.adgvcxz.bindTo
import com.adgvcxz.viewpagermodel.AppendData
import com.adgvcxz.viewpagermodel.RemoveData
import com.adgvcxz.viewpagermodel.ViewPagerAdapter
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_simple_view_pager.*

/**
 * zhaowei
 * Created by zhaowei on 2017/8/7.
 */
class SimpleViewPagerActivity : AppCompatActivity() {

    private val disposables: CompositeDisposable by lazy { CompositeDisposable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_view_pager)
        initViewModel()
    }

    private fun initViewModel() {
        val viewModel = SimpleViewPagerViewModel()
        val adapter = ViewPagerAdapter(viewModel, { ItemView() })
        viewPager.adapter = adapter
        add.clicks()
                .map { AppendData(arrayListOf(ItemViewModel())) }
                .bindTo(viewModel.action)
                .addTo(disposables)
        remove.clicks()
                .map { RemoveData(arrayListOf(0)) }
                .bindTo(viewModel.action)
                .addTo(disposables)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}
package com.adgvcxz.viewmodel.sample

import com.adgvcxz.addTo
import com.adgvcxz.bindTo
import com.adgvcxz.viewpagermodel.AppendData
import com.adgvcxz.viewpagermodel.RemoveData
import com.adgvcxz.viewpagermodel.ViewPagerAdapter
import com.adgvcxz.viewpagermodel.ViewPagerModel
import com.jakewharton.rxbinding4.view.clicks
import kotlinx.android.synthetic.main.activity_simple_view_pager.*

/**
 * zhaowei
 * Created by zhaowei on 2017/8/7.
 */
class SimpleViewPagerActivity : BaseActivity<SimpleViewPagerViewModel, ViewPagerModel>() {
    override val layoutId: Int = R.layout.activity_simple_view_pager

    override val viewModel: SimpleViewPagerViewModel = SimpleViewPagerViewModel()

    override fun initBinding() {
        val adapter = ViewPagerAdapter(viewModel, { ItemView() })
        viewPager.adapter = adapter
        add.clicks()
            .map { AppendData(arrayListOf(ItemViewModel())) }
            .bindTo(viewModel.action)
            .addTo(viewModel.disposables)
        remove.clicks()
            .map { RemoveData(arrayListOf(0)) }
            .bindTo(viewModel.action)
            .addTo(viewModel.disposables)
    }

}
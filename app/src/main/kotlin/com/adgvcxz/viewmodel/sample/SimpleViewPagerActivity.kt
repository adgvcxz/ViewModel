package com.adgvcxz.viewmodel.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.adgvcxz.viewpagermodel.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_simple_view_pager.*

/**
 * zhaowei
 * Created by zhaowei on 2017/8/7.
 */
class SimpleViewPagerActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_view_pager)
        initViewModel()
    }

    fun initViewModel() {
        val viewModel = SimpleViewPagerViewModel()
        val adapter = ViewPagerAdapter(viewModel) {
            Log.e("zhaow", "adapter")
            ItemView()
        }
        viewPager.adapter = adapter
    }
}
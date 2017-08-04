package com.adgvcxz.viewpagermodel

import android.database.DataSetObserver
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable

/**
 * zhaowei
 * Created by zhaowei on 2017/8/4.
 */
class ViewPagerAdapter(val viewModel: ViewPagerViewModel) : PagerAdapter() {

    var viewGroup: ViewGroup? = null

    private val disposables: CompositeDisposable by lazy { CompositeDisposable() }



    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return 1
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        super.destroyItem(container, position, `object`)
    }

    override fun startUpdate(container: ViewGroup) {
        super.startUpdate(container)
        if (viewGroup == null) {
            viewGroup = container
            viewGroup?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {
                    viewModel.currentModel().items.forEach { it.dispose() }
                    disposables.dispose()
                }

                override fun onViewAttachedToWindow(v: View?) {
                }
            })
        }
    }

}
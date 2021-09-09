package com.adgvcxz.viewpagermodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.adgvcxz.IModel
import com.adgvcxz.addTo
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlin.reflect.KClass

/**
 * zhaowei
 * Created by zhaowei on 2017/8/4.
 */
class ViewPagerAdapter(
    val viewModel: ViewPagerViewModel,
    private val configureItem: ((ViewPagerItemViewModel<out IModel>) -> IPagerItemView<*, *>),
    private val configureTitle: ((Int) -> CharSequence)? = null
) : PagerAdapter() {

    var viewGroup: ViewGroup? = null

    private val disposables: CompositeDisposable by lazy { CompositeDisposable() }
    private var viewMap: HashMap<Int, IPagerItemView<*, *>?> = HashMap()
    private val layoutMap: HashMap<KClass<ViewPagerItemViewModel<out IModel>>, Int> = HashMap()
    private var inflater: LayoutInflater? = null

    init {
        viewModel.model.map { it.items }
            .subscribe { notifyDataSetChanged() }
            .addTo(disposables)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun getCount(): Int = viewModel.currentModel().items.size

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (`object` is View) {
            container.removeView(`object`)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val model = viewModel.currentModel().items[position]
        var layoutId = layoutMap[model::class]
        if (layoutId == null) {
            val type = model::class as KClass<ViewPagerItemViewModel<out IModel>>
            val pagerItemView = configureItem.invoke(model)
            layoutMap.put(type, pagerItemView.layoutId)
            layoutId = pagerItemView.layoutId
            viewMap.put(layoutId, pagerItemView)
        }
        if (inflater == null) {
            inflater = LayoutInflater.from(container.context)
        }
        val view: View = inflater!!.inflate(layoutId, null, false)
        val holder = ViewPagerItemHolder(view)
        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                holder.disposables.dispose()
            }

            override fun onViewAttachedToWindow(v: View?) {
            }
        })
        container.addView(view)
        (viewMap[layoutId] as IPagerItemView<ViewPagerItemHolder, ViewPagerItemViewModel<out IModel>>)
            .bind(holder, viewModel.currentModel().items[position], position)
        return view
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

    override fun getItemPosition(`object`: Any): Int = POSITION_NONE

    override fun getPageTitle(position: Int): CharSequence? {
        val block = configureTitle
        return block?.invoke(position) ?: super.getPageTitle(position)
    }
}
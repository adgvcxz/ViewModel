package com.adgvcxz.viewmodel.sample

import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.adgvcxz.IModel
import com.adgvcxz.recyclerviewmodel.IView
import com.adgvcxz.recyclerviewmodel.ItemViewHolder
import com.adgvcxz.recyclerviewmodel.RecyclerItemViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * Created by zhaowei on 2021/10/18.
 */
class ItemBindingViewHolder<T : ViewBinding>(view: View, generateViewBinding: (View) -> T) :
    ItemViewHolder(view) {
    val binding: T = generateViewBinding(view)
}

interface ItemBindingView<T : ViewBinding, M : RecyclerItemViewModel<out IModel>> :
    IView<ItemBindingViewHolder<T>, M> {

    fun generateViewBinding(view: View): T

    fun bind(binding: T, viewModel: M, position: Int, disposable: CompositeDisposable)

    override fun initView(view: View, parent: ViewGroup): ItemBindingViewHolder<T> {
        return ItemBindingViewHolder(view, ::generateViewBinding)
    }

    override fun bind(viewHolder: ItemBindingViewHolder<T>, viewModel: M, position: Int) {
        super.bind(viewHolder, viewModel, position)
        bind(viewHolder.binding, viewModel, position, viewHolder.disposables)
    }

}
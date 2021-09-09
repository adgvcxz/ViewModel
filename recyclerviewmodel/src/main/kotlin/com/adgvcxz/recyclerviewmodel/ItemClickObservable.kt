package com.adgvcxz.recyclerviewmodel

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

/**
 * zhaowei
 * Created by zhaowei on 2017/5/15.
 */

class ItemClickObservable(private val adapter: RecyclerAdapter) : Observable<Int>() {


    override fun subscribeActual(observer: Observer<in Int>) {
        adapter.notifyDataSetChanged()
        adapter.itemClickListener = Listener(observer)
    }

    class Listener(private val observer: Observer<in Int>) : MainThreadDisposable(), View.OnClickListener {

        var recyclerView: RecyclerView? = null

        override fun onDispose() {
            if (recyclerView != null) {
                (0 until recyclerView!!.childCount)
                        .map { recyclerView!!.getChildAt(it) }
                        .forEach { it.setOnClickListener(null) }
            }
        }

        override fun onClick(v: View) {
            val parent = v.parent
            if (parent is RecyclerView && recyclerView == null) {
                recyclerView = parent
            }
            val holder = recyclerView?.getChildViewHolder(v)
            observer.onNext(holder?.adapterPosition ?: -1)
        }
    }

}
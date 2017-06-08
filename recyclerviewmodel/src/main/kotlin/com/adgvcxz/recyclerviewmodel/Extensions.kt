package com.adgvcxz.recyclerviewmodel

import android.support.v7.util.DiffUtil
import android.util.Log
import android.view.View
import com.adgvcxz.IModel
import com.adgvcxz.WidgetLifeCircleEvent
import com.adgvcxz.WidgetViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * zhaowei
 * Created by zhaowei on 2017/5/12.
 */

fun View.attach(): Observable<WidgetLifeCircleEvent> {
    return ViewAttachesObservable(this)
}

fun Observable<List<WidgetViewModel<out IModel>>>.bindTo(adapter: RecyclerAdapter): Disposable {
    return this.observeOn(Schedulers.computation())
            .scan(Pair<List<WidgetViewModel<out IModel>>,
                    DiffUtil.DiffResult?>(adapter.viewModel.currentModel.items, null)) { (first), list ->
                if (adapter.viewModel.currentModel.isAnim) {
                    val diff = ItemDiffCallback(first, list)
                    val result = DiffUtil.calculateDiff(diff, true)
                    Pair(list, result)
                } else {
                    Pair(list, null)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(adapter)
}
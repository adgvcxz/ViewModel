package com.adgvcxz.recyclerviewmodel

import androidx.recyclerview.widget.DiffUtil
import com.adgvcxz.IModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * zhaowei
 * Created by zhaowei on 2017/5/12.
 */

fun Observable<List<RecyclerItemViewModel<out IModel>>>.bindTo(adapter: RecyclerAdapter): Disposable {
    return this.observeOn(Schedulers.computation())
            .scan(Pair<List<RecyclerItemViewModel<out IModel>>,
                    DiffUtil.DiffResult?>(adapter.viewModel.currentModel().items, null)) { (first), list ->
                val diff = ItemDiffCallback(first, list)
                val result = DiffUtil.calculateDiff(diff, true)
                list to result
            }
            .skip(1)
            .map { it.second!! }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(adapter)
}


fun <T1, T2> ifNotNull(value1: T1?, value2: T2?, bothNotNull: (T1, T2) -> (Unit)) {
    if (value1 != null && value2 != null) {
        bothNotNull(value1, value2)
    }
}

fun RecyclerAdapter.itemClicks(): Observable<Int> {
    if (action == null) {
        action = PublishSubject.create<Int>().toSerialized()
    }
    return action!!
}
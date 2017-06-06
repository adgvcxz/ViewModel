package com.adgvcxz.recyclerviewmodel

import android.view.View
import com.adgvcxz.WidgetLifeCircleEvent
import io.reactivex.Observable

/**
 * zhaowei
 * Created by zhaowei on 2017/5/12.
 */

fun View.attach(): Observable<WidgetLifeCircleEvent> {
    return ViewAttachesObservable(this)
}
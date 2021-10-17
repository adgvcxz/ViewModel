package com.adgvcxz

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/**
 * zhaowei
 * Created by zhaowei on 2017/4/30.
 */

interface IEvent

data class LifecycleEventChanged(val source: LifecycleOwner, val event: Lifecycle.Event): IEvent, IMutation
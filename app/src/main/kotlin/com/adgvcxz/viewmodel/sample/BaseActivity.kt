package com.adgvcxz.viewmodel.sample

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

abstract class BaseActivity : AppCompatActivity(), LifecycleRegistryOwner {

    val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        initBinding()
    }

    open fun initBinding() {

    }

    override fun getLifecycle(): LifecycleRegistry {
        return lifecycleRegistry
    }
}

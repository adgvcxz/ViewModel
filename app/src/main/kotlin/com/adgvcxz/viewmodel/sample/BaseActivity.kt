package com.adgvcxz.viewmodel.sample

//import android.arch.lifecycle.LifecycleRegistry
//import android.arch.lifecycle.LifecycleRegistryOwner
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable

/**
 * zhaowei
 * Created by zhaowei on 2017/4/27.
 */

abstract class BaseActivity : AppCompatActivity()/*, LifecycleRegistryOwner*/ {

//    val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    abstract val layoutId: Int
    val disposables: CompositeDisposable by lazy { CompositeDisposable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        initBinding()
    }

    open fun initBinding() {

    }

//    override fun getLifecycle(): LifecycleRegistry {
//        return lifecycleRegistry
//    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}

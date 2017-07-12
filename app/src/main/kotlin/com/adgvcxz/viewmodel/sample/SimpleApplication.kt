package com.adgvcxz.viewmodel.sample

import android.app.Application
import com.squareup.leakcanary.LeakCanary

/**
 * zhaowei
 * Created by zhaowei on 2017/6/9.
 */
class SimpleApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

}
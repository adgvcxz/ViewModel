package com.adgvcxz.viewmodel.sample

import android.util.Log
import com.adgvcxz.IAction
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import com.adgvcxz.ViewModel
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * zhaowei
 * Created by zhaowei on 2017/5/9.
 */

class TestActivityModel: ViewModel<TestActivityModel.Model>(Model()) {

    class Model: IModel {
        var number: Int = 0
    }

    enum class Action: IAction {
        StartButtonDidClicked
    }

    enum class Mutation: IMutation {
        Timer
    }

    override fun mutate(action: IAction): Observable<IMutation> {
        return Observable.just(Mutation.Timer).delay(1, TimeUnit.SECONDS).repeat(10).map { it }
    }

    override fun scan(model: Model, mutation: IMutation): Model {
        Log.e("zhaow", "scan")
        model.number += 1
        return model
    }


}
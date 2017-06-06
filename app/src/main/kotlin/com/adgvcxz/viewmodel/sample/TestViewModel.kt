package com.adgvcxz.viewmodel.sample

import android.arch.lifecycle.MutableLiveData
import com.adgvcxz.IEvent
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import com.adgvcxz.AFViewModel
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * zhaowei
 * Created by zhaowei on 2017/5/9.
 */

class TestViewModel : AFViewModel<TestViewModel.Model>() {

    override val initModel: Model = Model()

    class Model : IModel {
        var number: MutableLiveData<Int> = MutableLiveData()

        init {
            number.postValue(0)
        }
    }

    enum class Event : IEvent {
        StartButtonDidClicked
    }

    enum class Mutation : IMutation {
        Timer
    }

    override fun mutate(event: IEvent): Observable<IMutation> {
        return Observable.just(Mutation.Timer).delay(1, TimeUnit.SECONDS).repeat(10).map { it }
    }

    override fun scan(model: Model, mutation: IMutation): Model {
        model.number.value = model.number.value!! + 1
        return model
    }


}
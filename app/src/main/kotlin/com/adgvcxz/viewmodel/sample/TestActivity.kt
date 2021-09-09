package com.adgvcxz.viewmodel.sample

import android.os.Bundle
import com.adgvcxz.AFViewModel
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import com.adgvcxz.addTo
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*

/**
 * zhaowei
 * Created by zhaowei on 2017/7/12.
 */

class TestActivity : BaseActivity() {
    override val layoutId: Int get() = R.layout.activity_test

    lateinit var viewModel: TestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = TestViewModel(intent.getIntExtra("id", 0), intent.getStringExtra("value") ?: "")
        super.onCreate(savedInstanceState)
    }

    override fun initBinding() {
        button.clicks()
                .doOnNext { RxBus.instance.post(ValueChangeEvent(12, UUID.randomUUID().toString())) }
                .subscribe()
                .addTo(disposables)

        viewModel.model.map { it.value }
                .distinctUntilChanged()
                .subscribe { testTextView.text = it }
                .addTo(disposables)

        Observable.just("abcd")
                .compose {
                    Observable.merge(it, RxBus.instance
                            .toObservable(ValueChangeEvent::class.java)
                            .map { it.value })

                }
                .subscribe { button.text = it }
                .addTo(disposables)
    }

}

class TextModel(var id: Int, var value: String) : IModel

class ValueChangeMutation(val value: String) : IMutation

class TestViewModel(id: Int, value: String) : AFViewModel<TextModel>() {
    override val initModel: TextModel = TextModel(id, value)

    override fun transformMutation(mutation: Observable<IMutation>): Observable<IMutation> {
        val add = RxBus.instance.toObservable(ValueChangeEvent::class.java)
                .map { ValueChangeMutation(it.value) }
        return Observable.merge(add, mutation)
    }

    override fun scan(model: TextModel, mutation: IMutation): TextModel {
        when (mutation) {
            is ValueChangeMutation -> model.value = mutation.value
        }
        return model
    }
}
package com.adgvcxz.viewmodel.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.adgvcxz.bindTo
import com.adgvcxz.recyclerviewmodel.RecyclerAdapter
import com.adgvcxz.recyclerviewmodel.RecyclerViewModel
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.activity_simple_recycler.*

class SimpleRecyclerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_recycler)
        val viewModel = SimpleRecyclerViewModel()
        val adapter = RecyclerAdapter(viewModel) {
            when (it) {
                is TextItemViewModel -> TextItemView()
                else -> LoadingItemView()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        appendData.clicks()
                .map { RecyclerViewModel.Event.loadMore }
                .bindTo(viewModel.action)
    }
}

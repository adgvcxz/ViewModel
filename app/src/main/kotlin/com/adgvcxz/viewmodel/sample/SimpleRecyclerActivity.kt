package com.adgvcxz.viewmodel.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.adgvcxz.addTo
import com.adgvcxz.bindTo
import com.adgvcxz.recyclerviewmodel.RecyclerAdapter
import com.adgvcxz.recyclerviewmodel.RecyclerViewModel
import com.adgvcxz.recyclerviewmodel.itemClicks
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.jakewharton.rxbinding2.support.v4.widget.refreshing
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_simple_recycler.*
import kotlinx.android.synthetic.main.item_text_view.view.*
import java.util.concurrent.TimeUnit
import com.adgvcxz.viewmodel.sample.R.id.recyclerView
import android.support.v7.widget.SimpleItemAnimator



class SimpleRecyclerActivity : AppCompatActivity() {

    val disposables: CompositeDisposable by lazy { CompositeDisposable() }

    var data: List<Int> = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_recycler)
        initViewModel()
//        initAdapter()
    }

    private fun initViewModel() {
        val viewModel = SimpleRecyclerViewModel()
        val adapter = RecyclerAdapter(viewModel) {
            when (it) {
                is TextItemViewModel -> TextItemView()
                else -> LoadingItemView()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerView.itemAnimator.changeDuration = 0

        refreshLayout.refreshes()
                .map { RecyclerViewModel.Event.refresh }
                .bindTo(viewModel.action)
                .addTo(disposables)

        adapter.itemClicks()
                .filter { it == adapter.itemCount - 1 }
                .map { RecyclerViewModel.Event.loadMore }
                .bindTo(viewModel.action)
                .addTo(disposables)

        adapter.itemClicks()
                .filter { it != adapter.itemCount - 1 }
                .subscribe { adapter.notifyDataSetChanged() }
                .addTo(disposables)

        viewModel.model.map { it.isRefresh }
                .filter { it != refreshLayout.isRefreshing }
                .subscribe(refreshLayout.refreshing())
                .addTo(disposables)

        viewModel.action.onNext(RecyclerViewModel.Event.refresh)
    }

//    fun initAdapter() {
//        val adapter = SimpleAdapter()
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = adapter
//    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    inner class SimpleAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            holder!!.itemView.textView.text = "${data[position]}"
            holder.itemView.setOnClickListener {
                data = data.subList(1, data.size)
                notifyItemRemoved(0)
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_text_view, parent, false)){}
        }

    }
}

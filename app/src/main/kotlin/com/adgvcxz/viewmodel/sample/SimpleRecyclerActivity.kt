package com.adgvcxz.viewmodel.sample

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.adgvcxz.add
import com.adgvcxz.addTo
import com.adgvcxz.bindEvent
import com.adgvcxz.bindModel
import com.adgvcxz.recyclerviewmodel.*
import com.jakewharton.rxbinding4.swiperefreshlayout.refreshes
import kotlinx.android.synthetic.main.activity_simple_recycler.*


class SimpleRecyclerActivity : BaseActivity<SimpleRecyclerViewModel, RecyclerModel>() {
    override val layoutId: Int = R.layout.activity_simple_recycler

    override val viewModel: SimpleRecyclerViewModel = SimpleRecyclerViewModel()


    override fun initBinding() {

        val adapter = RecyclerAdapter(viewModel) {
            when (it) {
                is TextItemViewModel -> TextItemView()
                else -> LoadingItemView()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
//        recyclerView.setHasFixedSize(true)

        viewModel.bindEvent {
            add({ refreshLayout.refreshes() }, { Refresh })
        }

        adapter.itemClicks()
            .filter { viewModel.currentModel().items[it].currentModel() is TextItemViewModel.Model }
            .map { viewModel.currentModel().items[it].currentModel() as TextItemViewModel.Model }
            .subscribe {
                if (it.id == 0) {
                    val intent = Intent(this, TestActivity::class.java)
                    intent.putExtra("id", it.id)
                    intent.putExtra("value", it.content)
                    startActivity(intent)
                } else {
                    viewModel.action.onNext(
                        ReplaceData(
                            arrayListOf(it.id),
                            arrayListOf(TextItemViewModel())
                        )
                    )
                }
            }
            .addTo(viewModel.disposables)

//        adapter.itemClicks()
//                .filter { it != adapter.itemCount - 1 }
//                .map { RecyclerViewModel.RemoveDataEvent(arrayListOf(it)) }
//                .bindTo(viewModel.action)
//                .addTo(disposables)

//        adapter.itemClicks()
//                .filter { it != adapter.itemCount - 1 }
//                .subscribe { adapter.notifyDataSetChanged() }
//                .addTo(disposables)

//        adapter.itemClicks()
//                .filter { it != adapter.itemCount - 1 }
//                .map { RecyclerViewModel.ReplaceDataEvent(arrayListOf(it), arrayListOf(TextItemViewModel())) }
//                .bindTo(viewModel.action)
//                .addTo(disposables)

        viewModel.bindModel {
            add({ isRefresh }, { refreshLayout.isRefreshing = this })
        }


        viewModel.action.onNext(Refresh)

    }

}

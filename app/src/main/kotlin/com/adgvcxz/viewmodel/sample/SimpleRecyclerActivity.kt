package com.adgvcxz.viewmodel.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.adgvcxz.recyclerviewmodel.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_simple_recycler.*

class SimpleRecyclerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_recycler)
        val adapter = RecyclerAdapter(SimpleRecyclerViewModel()) { TextItemView() }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}

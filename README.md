## ViewModel 

#### Temporarily remove the Android official ViewModel, because the compilation is very slow

##### 1. Configure RecyclerView

* Initialization

```
val viewModel = SimpleRecyclerViewModel()
//multi-type
val adapter = RecyclerAdapter(viewModel) {
    when (it) {
        is TextItemViewModel -> TextItemView()
        else -> LoadingItemView()
    }
}
recyclerView.layoutManager = LinearLayoutManager(this)
recyclerView.adapter = adapter
```

* ItemClickListener

```
adapter.itemClicks().subscribe()
```
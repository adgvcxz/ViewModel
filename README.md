## ViewModel 

### 以RxJava为基础，分离Model与View，以Event来驱动更新Model和View

#### 支持Activity Fragment RecyclerView ViewPager 以及普通自定义View

##### 支持DSL:

```koltin
viewModel.toEvents {
    section<Unit, View> {
        observable = { clicks() }
        item {
            event { TimerViewModel.Event.StartButtonClicked }
            view = start
        }
        item {
            event { TimerViewModel.Event.StopButtonClicked }
            view = stop
        }
    }
}.addTo(disposables)

viewModel.toBuilder {
    section<TimerViewModel.Model> {
        mapItem<String> {
            value { this }
            filter { filter { it.status == TimerViewModel.TimerStatus.Completed } }
            map { "Timer" }
            behavior = time.text()
        }
        mapItem<String> {
            value { this }
            filter { filter { it.status == TimerViewModel.TimerStatus.Timing } }
            map { "$time" }
            behavior = time.text()
        }
    }
}.addTo(disposables)
```

##### 以RecyclerView为例:

* 初始化

```kotlin
val viewModel = SimpleRecyclerViewModel()
// 多类型
// 配置每个ViewModel对应的View
val adapter = RecyclerAdapter(viewModel) {
    when (it) {
        is TextItemViewModel -> TextItemView()
        else -> LoadingItemView()
    }
}
recyclerView.layoutManager = LinearLayoutManager(this)
recyclerView.adapter = adapter

...

class TextItemView : IDefaultView<TextItemViewModel> {
    override fun bind(viewHolder: ItemViewHolder, viewModel: TextItemViewModel, position: Int) {
        //绑定View与Model
        ...
    }
}
```

* 更新
```kotlin
//刷新数据
viewmodel.action.onNext(SetData(arrayListOf()))

//在尾部插入数据
viewodel.action.onNext(AppendData(arrayListOf()))

等等...
```
也可以自定义想要的操作，仅仅发送一次事件，无需`notifyDataSetChanged()`或者`notifyItemRangeInserted()`等操作

内部已经实现自动`Refresh`或者`LoadMore`等操作，只需要
1. 绑定SwipeRefreshLayout
```kotlin
refreshLayout.refreshes()
                .map { Refresh }
                .bindTo(viewModel.action)
                .addTo(disposables)
```
2. 实现request方法
```java
override fun request(refresh: Boolean): Observable<IMutation> {
    //refresh: 
    //true: 下拉刷新数据
    //false: 列表在底部加载更多数据
    ...
}
```

* ItemClickListener(可以重复监听)

```
adapter.itemClicks().subscribe()

adapter.itemClicks().subscribe()
```

* 同步列表页面和详情页面的Model和View 同样只需要定义事件以及做好监听


#### 导入
	allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
	}
	
	dependencies {
	    compile 'com.github.adgvcxz.viewModel:viewmodel:0.6.0'
        compile 'com.github.adgvcxz.viewModel:recyclerviewmodel:0.6.0'
        compile 'com.github.adgvcxz.viewModel:viewpagermodel:0.6.0'
    }
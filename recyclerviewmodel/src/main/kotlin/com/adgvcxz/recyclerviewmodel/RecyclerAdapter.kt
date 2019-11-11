package com.adgvcxz.recyclerviewmodel

import android.os.Handler
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import com.adgvcxz.addTo
import com.adgvcxz.bindTo
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.Subject
import kotlin.reflect.KClass

/**
 * zhaowei
 * Created by zhaowei on 2017/6/5.
 */
open class RecyclerAdapter(val viewModel: RecyclerViewModel,
                           private val configureItem: ((RecyclerItemViewModel<out IModel>) -> IView<*, *>)) :
        RecyclerView.Adapter<ItemViewHolder>(),
        Consumer<DiffUtil.DiffResult> {

    private var inflater: LayoutInflater? = null
    private var viewMap: HashMap<Int, IView<*, *>?> = HashMap()
    private val layoutMap: HashMap<KClass<RecyclerItemViewModel<out IModel>>, Int> = HashMap()
    var items: List<RecyclerItemViewModel<out IModel>> = emptyList()
    internal var itemClickListener: View.OnClickListener? = null
    internal var action: Subject<Int>? = null
    private var notify: Boolean = false
    private var loading: Boolean = false
    val disposables: CompositeDisposable by lazy { CompositeDisposable() }
    val holders = mutableListOf<ItemViewHolder>()
    open var isAttachToBind = true
    open var disposeWhenDetached = true
    private val handler = Handler()

    init {
//        setHasStableIds(true)
        initItems()
        items = viewModel.currentModel().items
        viewModel.changed = { model, mutation ->
            if (viewModel.currentModel().isAnim) {
                handler.post { updateByMutation(model.items, mutation) }
            }
        }
    }

    private fun initItems() {
        viewModel.model.map { it.items }
                .filter { !viewModel.currentModel().isAnim }
                .subscribe {
                    updateData()
                }.addTo(disposables)

        itemClicks()
                .filter { items[it] is LoadingItemViewModel }
                .map { LoadMore }
                .bindTo(viewModel.action)
                .addTo(disposables)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val view = inflater!!.inflate(viewType, parent, false)
        ifNotNull(view, itemClickListener) { _, listener -> view.setOnClickListener(listener) }
        return viewMap[viewType]!!.initView(view, parent)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        action?.let { holder.itemView.setOnClickListener { _ -> it.onNext(position) } }
        if (!isAttachToBind) {
            val iView = viewMap[getItemViewType(holder.layoutPosition)]
            iView?.let {
                holder.disposables.clear()
                holders.add(holder)
                (it as IView<in ItemViewHolder, RecyclerItemViewModel<out IModel>>)
                        .bind(holder, items[holder.layoutPosition], holder.layoutPosition)
            }
        }
        checkLoadMore(position)
    }

    override fun getItemCount(): Int = items.size

    @Suppress("UNCHECKED_CAST")
    override fun getItemViewType(position: Int): Int {
        val model = items[position]
        var id = layoutMap[model::class]
        if (id == null) {
            val type = model::class as KClass<RecyclerItemViewModel<out IModel>>
            val view = configureItem.invoke(model)
            layoutMap.put(type, view.layoutId)
            id = view.layoutId
            viewMap.put(id, view)
        }
        return id
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewAttachedToWindow(holder: ItemViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.layoutPosition != NO_POSITION && isAttachToBind) {
            val iView = viewMap[getItemViewType(holder.layoutPosition)]
            iView?.let {
                (it as IView<in ItemViewHolder, RecyclerItemViewModel<out IModel>>)
                        .bind(holder, items[holder.layoutPosition], holder.layoutPosition)
            }
//            viewModel.currentModel().items[holder.layoutPosition].action.onNext(WidgetLifeCircleEvent.Attach)
        }
    }

    override fun onViewDetachedFromWindow(holder: ItemViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder.layoutPosition != NO_POSITION) {
//            viewModel.currentModel().items[holder.layoutPosition].action.onNext(WidgetLifeCircleEvent.Detach)
            holders.remove(holder)
            holder.disposables.clear()
        }
    }


    private fun checkLoadMore(position: Int) {
        val loadingModel = viewModel.currentModel().loadingViewModel
        loadingModel?.let {
            loading = if ((position == itemCount - 1) && !viewModel.currentModel().isLoading && !loading) {
                viewModel.action.onNext(LoadMore)
                true
            } else {
                false
            }
        }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                if (disposeWhenDetached) {
                    items.forEach { it.dispose() }
                    disposables.dispose()
                    holders.forEach { it.disposables.dispose() }
                    (0 until recyclerView.childCount).forEach {
                        val view = recyclerView.getChildAt(it)
                        val holder = (recyclerView.getChildViewHolder(view) as ItemViewHolder)
                        holder.disposables.dispose()
                    }
                } else {
                    disposables.clear()
                    holders.forEach { it.disposables.clear() }
                    (0 until recyclerView.childCount).forEach {
                        val view = recyclerView.getChildAt(it)
                        val holder = (recyclerView.getChildViewHolder(view) as ItemViewHolder)
                        holder.disposables.clear()
                    }
                }
            }

            override fun onViewAttachedToWindow(v: View?) {
                if (!disposeWhenDetached && disposables.size() == 0) {
                    initItems()
                    notifyDataSetChanged()
                }
            }
        })
    }

//    override fun getItemId(position: Int): Long {
//        return viewModel.currentModel().items[position]._id
//    }

    override fun accept(result: DiffUtil.DiffResult) {
        updateData()
        notify = viewModel.currentModel().isRefresh
    }

    private fun updateData() {
        items = viewModel.currentModel().items
        notifyDataSetChanged()
    }

    open fun updateByMutation(items: List<RecyclerItemViewModel<out IModel>>, mutation: IMutation) {
        this.items = items
        when (mutation) {
            is SetData -> notifyDataSetChanged()
            is ReplaceData -> {
                val min = mutation.index.min()
                val max = mutation.index.max()
                if (min != null && max != null) {
                    notifyItemRangeChanged(min, max)
                }
            }
            is AppendData -> {
                val first = mutation.data.firstOrNull()
                if (first != null) {
                    val firstIndex = items.indexOf(first)
                    if (firstIndex >= 0) {
                        notifyItemRangeInserted(firstIndex, mutation.data.size)
                    }
                }
            }
            is InsertData -> {
                notifyItemRangeInserted(mutation.index, mutation.data.size)
            }
            is RemoveData -> {
                for (index in mutation.index) {
                    notifyItemRemoved(index)
                }
            }
        }
    }
}
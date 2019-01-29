package com.adgvcxz.recyclerviewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.adgvcxz.IModel
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
class RecyclerAdapter(val viewModel: RecyclerViewModel,
                      private val configureItem: ((RecyclerItemViewModel<out IModel>) -> IView<*, *>)) :
        RecyclerView.Adapter<ItemViewHolder>(),
        Consumer<DiffUtil.DiffResult> {

    private var inflater: LayoutInflater? = null
    private var viewMap: HashMap<Int, IView<*, *>?> = HashMap()
    private val layoutMap: HashMap<KClass<RecyclerItemViewModel<out IModel>>, Int> = HashMap()
    private val items: MutableList<RecyclerItemViewModel<out IModel>> = arrayListOf()
    internal var itemClickListener: View.OnClickListener? = null
    internal var action: Subject<Int>? = null
    private var notify: Boolean = false
    private var loading: Boolean = false
    private val disposables: CompositeDisposable by lazy { CompositeDisposable() }
    private val holders = arrayListOf<ItemViewHolder>()
    var isAttachToBind = true

    init {
//        setHasStableIds(true)
        viewModel.model.map { it.items }
                .bindTo(this)
                .addTo(disposables)

        itemClicks()
                .filter { items[it] is LoadingItemViewModel }
                .map { LoadMore }
                .bindTo(viewModel.action)
                .addTo(disposables)
        viewModel.currentModel().items.mapTo(items) { it }
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
                items.forEach {
                    it.dispose()
                }
                disposables.dispose()
                holders.forEach { it.disposables.dispose() }
                (0 until recyclerView.childCount).forEach {
                    val view = recyclerView.getChildAt(it)
                    val holder = (recyclerView.getChildViewHolder(view) as ItemViewHolder)
                    holder.disposables.dispose()
                }
            }

            override fun onViewAttachedToWindow(v: View?) {
            }
        })
    }

//    override fun getItemId(position: Int): Long {
//        return viewModel.currentModel().items[position]._id
//    }

    override fun accept(result: DiffUtil.DiffResult) {
        if (viewModel.currentModel().isAnim && !notify) {
            items.clear()
            viewModel.currentModel().items.mapTo(items) { it }
            result.dispatchUpdatesTo(this)
        } else {
            result.dispatchUpdatesTo(object : ListUpdateCallback {
                override fun onChanged(position: Int, count: Int, payload: Any?) {
                    updateData()
                }

                override fun onMoved(fromPosition: Int, toPosition: Int) {
                    updateData()
                }

                override fun onInserted(position: Int, count: Int) {
                    updateData()
                }

                override fun onRemoved(position: Int, count: Int) {
                    updateData()
                }
            })
        }
        notify = viewModel.currentModel().isRefresh
    }

    private fun updateData() {
        items.clear()
        viewModel.currentModel().items.mapTo(items) { it }
        notifyDataSetChanged()
    }
}
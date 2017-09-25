package com.adgvcxz.recyclerviewmodel

import android.support.v7.util.DiffUtil
import android.support.v7.util.ListUpdateCallback
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                .filter { viewModel.currentModel().items[it] is LoadingItemViewModel }
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
                        .bind(holder, viewModel.currentModel().items[holder.layoutPosition], holder.layoutPosition)
            }
        }
        checkLoadMore(position)
    }

    override fun getItemCount(): Int = viewModel.count

    @Suppress("UNCHECKED_CAST")
    override fun getItemViewType(position: Int): Int {
        val model = viewModel.currentModel().items[position]
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
                        .bind(holder, viewModel.currentModel().items[holder.layoutPosition], holder.layoutPosition)
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
            if ((position == itemCount - 1) && !viewModel.currentModel().isLoading && !loading) {
                viewModel.action.onNext(LoadMore)
                loading = true
            } else {
                loading = false
            }
        }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                viewModel.currentModel().items.forEach {
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
            result.dispatchUpdatesTo(this)
        } else {
            result.dispatchUpdatesTo(object : ListUpdateCallback {
                override fun onChanged(position: Int, count: Int, payload: Any?) {
                    notifyDataSetChanged()
                }

                override fun onMoved(fromPosition: Int, toPosition: Int) {
                    notifyDataSetChanged()
                }

                override fun onInserted(position: Int, count: Int) {
                    notifyDataSetChanged()
                }

                override fun onRemoved(position: Int, count: Int) {
                    notifyDataSetChanged()
                }
            })
        }
        notify = viewModel.currentModel().isRefresh
    }
}
package com.adgvcxz.recyclerviewmodel

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adgvcxz.IModel
import com.adgvcxz.WidgetLifeCircleEvent
import com.adgvcxz.WidgetViewModel
import io.reactivex.functions.Consumer
import kotlin.reflect.KClass

/**
 * zhaowei
 * Created by zhaowei on 2017/6/5.
 */
class RecyclerAdapter(private val viewModel: RecyclerViewModel, private val configureItem: ((WidgetViewModel<out IModel>) -> IView<*>)) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Consumer<List<WidgetViewModel<out IModel>>> {


    private var inflater: LayoutInflater? = null
    private var viewMap: HashMap<View, IView<*>?> = HashMap()
    private val layoutMap: HashMap<KClass<WidgetViewModel<out IModel>>, Int> = HashMap()

    private lateinit var iView: IView<*>

    init {
        viewModel.model.map { it.items }
                .subscribe(this)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val view = inflater!!.inflate(viewType, parent, false)
        viewMap.put(view, iView)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int = viewModel.count

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val iView = viewMap[holder.itemView]
        (iView as IView<WidgetViewModel<out IModel>>)
                .bind(holder.itemView, viewModel.currentModel.items[position])
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.adapterPosition != RecyclerView.NO_POSITION) {
            viewModel.currentModel.items[holder.adapterPosition].action.onNext(WidgetLifeCircleEvent.Attach)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder.adapterPosition != RecyclerView.NO_POSITION) {
            viewModel.currentModel.items[holder.adapterPosition].action.onNext(WidgetLifeCircleEvent.Detach)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getItemViewType(position: Int): Int {
        var id = layoutMap[viewModel.currentModel.items[position]::class]
        if (id == null) {
            iView = configureItem.invoke(viewModel.currentModel.items[position])
            layoutMap.put(viewModel.currentModel.items[position]::class as KClass<WidgetViewModel<out IModel>>,
                    iView.layoutId)
            id = iView.layoutId
        }
        return id
    }

    override fun accept(t: List<WidgetViewModel<out IModel>>) {
        notifyDataSetChanged()
    }

}
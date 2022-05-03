package com.herry.libs.nodeview.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.herry.libs.nodeview.INodeRoot
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodeNotify
import com.herry.libs.nodeview.model.NodePosition
import com.herry.libs.nodeview.model.NodeRoot

@Suppress("unused")
abstract class NodeRecyclerAdapter(
    protected val context: () -> Context, log: Boolean = false
) : RecyclerView.Adapter<NodeRecyclerHolder>(),
    INodeRoot {

    private val viewTypeToForms = mutableMapOf<Int, NodeForm<out NodeHolder, *>>()

    override val root = NodeRoot(object : NodeNotify {
        override fun nodeSetChanged() {
            notifyDataSetChanged()
        }

        override fun nodeMoved(from: Int, to: Int) {
            notifyItemMoved(from, to)
        }

        override fun nodeChanged(position: Int, count: Int) {
            notifyItemRangeChanged(position, count)
        }

        override fun nodeInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun nodeRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

        override fun nodeEndTransition() {}
    }, log)

    init {
        this.setHasStableIds(true)
        this.bindForms()
    }

    private fun bindForms() {
        val formsList = mutableListOf<NodeForm<out NodeHolder, *>>()
        onBindForms(formsList)
        for (i in formsList.indices) {
            viewTypeToForms[i + 1] = formsList[i]
        }
    }

    protected abstract fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>)

    override fun getItemCount(): Int = root.getViewCount()

    override fun getItemId(position: Int): Long {
        val itemId = root.getNodePosition(position)?.let {
            root.getNode(it)?.getNodeId()
        }
        return itemId ?: super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        val model = getBindModel(position)
        model?.let {
            for (viewTypeToForm in viewTypeToForms) {
                if (viewTypeToForm.value.mClass.isInstance(it)) {
                    return viewTypeToForm.key
                }
            }
        }
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NodeRecyclerHolder {
        val form = viewTypeToForms[p1]
        val holder = form?.let {
            form.createHolder(context(), p0, false)
        }
        holder?.let {
            return NodeRecyclerHolder(it)
        } ?: let {
            val tv = TextView(context()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                gravity = Gravity.CENTER

                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
                setPadding(20, 20, 20, 20)
                setBackgroundColor(Color.DKGRAY)
            }

            return NodeRecyclerHolder(NodeHolder(context(), tv))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: NodeRecyclerHolder, p1: Int) {
        getBindNode(p1)?.let { node ->
            val form = viewTypeToForms[p0.itemViewType]

            when {
                form != null -> {
                    if (node.model is NodeRecyclerSingleModel && p0.itemId == node.getNodeId()) {
                        if (!(node.model as NodeRecyclerSingleModel).updated) {
                            (node.model as NodeRecyclerSingleModel).updated = true
                            form.bindModel(context(), p0.holder, node.model)
                        }
                    } else {
                        form.bindModel(context(), p0.holder, node.model)
                    }
                }
                p0.itemView is TextView -> {
                    p0.itemView.text = "position : ${p1}\nmodel : ${node.model::class.simpleName}"
                }
            }
        }
    }

    fun getNodePosition(holder: NodeRecyclerHolder): NodePosition? =
        getNodePosition(holder.adapterPosition)

    fun getNodePosition(position: Int): NodePosition? = root.getNodePosition(position)

    fun getBindModel(position: Int): Any? {
        return root.getNodePosition(position)?.let {
            root.getNode(it)?.model
        }
    }

    fun getBindNode(position: Int): Node<*>? {
        return root.getNodePosition(position)?.let {
            root.getNode(it)
        }
    }

    fun getNode(holder: NodeRecyclerHolder): Node<*>? {
        val position = getNodePosition(holder)
        return if (position != null) {
            root.getNode(position)
        } else null
    }

    override fun onViewRecycled(holder: NodeRecyclerHolder) {
        super.onViewRecycled(holder)

        if (viewTypeToForms[holder.itemViewType] is NodeRecyclerForm) {
            (viewTypeToForms[holder.itemViewType] as NodeRecyclerForm).onViewRecycled(
                context(),
                holder.holder
            )
        }
    }

    override fun onViewAttachedToWindow(holder: NodeRecyclerHolder) {
        super.onViewAttachedToWindow(holder)

        if (viewTypeToForms[holder.itemViewType] is NodeRecyclerForm) {
            (viewTypeToForms[holder.itemViewType] as NodeRecyclerForm).onViewAttachedToWindow(
                context(),
                holder.holder
            )
        }
    }

    override fun onViewDetachedFromWindow(holder: NodeRecyclerHolder) {
        super.onViewDetachedFromWindow(holder)

        if (viewTypeToForms[holder.itemViewType] is NodeRecyclerForm) {
            (viewTypeToForms[holder.itemViewType] as NodeRecyclerForm).onViewDetachedFromWindow(
                context(),
                holder.holder
            )
        }
    }
}
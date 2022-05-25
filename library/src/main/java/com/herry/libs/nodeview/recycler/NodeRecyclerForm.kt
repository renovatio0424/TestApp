package com.herry.libs.nodeview.recycler

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodePosition
import kotlin.reflect.full.safeCast

@Suppress("MemberVisibilityCanBePrivate", "unused")
interface NodeRecyclerForm {

    companion object {
        private fun getPosition(holder: NodeHolder): Int = holder.position?.invoke() ?: 0

        fun getNodePosition(holder: NodeHolder): NodePosition? =
            getAdapter(holder)?.getNodePosition(getPosition(holder))

        fun getNodePosition(nodeRecyclerHolder: NodeRecyclerHolder): NodePosition? =
            getAdapter(nodeRecyclerHolder)?.getNodePosition(getPosition(nodeRecyclerHolder.holder))

        fun <H : NodeHolder, M : Any> getBindNode(
            form: NodeForm<H, M>,
            holder: NodeHolder
        ): Node<M>? = getAdapter(holder)?.getBindNode(getPosition(holder))?.let { node ->
            if (form.mClass.isInstance(node.model)) {
                @Suppress("UNCHECKED_CAST")
                node as Node<M>
            } else {
                null
            }
        }

        fun <H : NodeHolder, M : Any> getBindNode(
            form: NodeForm<H, M>,
            nodeRecyclerHolder: NodeRecyclerHolder
        ): Node<M>? = getAdapter(nodeRecyclerHolder)?.getBindNode(getPosition(nodeRecyclerHolder.holder))?.let { node ->
            if (form.mClass.isInstance(node.model)) {
                @Suppress("UNCHECKED_CAST")
                node as Node<M>
            } else {
                null
            }
        }

        fun <H : NodeHolder, M : Any> getBindModel(form: NodeForm<H, M>, holder: NodeHolder): M? =
            getAdapter(holder)?.getBindNode(getPosition(holder))?.let { node ->
                form.mClass.safeCast(node.model)
            }

        fun <H : NodeHolder, M : Any> getBindModel(form: NodeForm<H, M>, nodeRecyclerHolder: NodeRecyclerHolder): M? {
            return getAdapter(nodeRecyclerHolder)?.getBindNode(getPosition(nodeRecyclerHolder.holder))?.let { node ->
                form.mClass.safeCast(node.model)
            }
        }

        fun getAdapter(holder: NodeHolder): NodeRecyclerAdapter? = (holder.view.parent as? RecyclerView)?.adapter as? NodeRecyclerAdapter

        fun getAdapter(nodeRecyclerHolder: NodeRecyclerHolder): NodeRecyclerAdapter? {
            return  (nodeRecyclerHolder.holder.view.parent as? RecyclerView)?.adapter as? NodeRecyclerAdapter
                ?: nodeRecyclerHolder.bindingAdapter as? NodeRecyclerAdapter
        }
    }

    fun onViewRecycled(context: Context, nodeRecyclerHolder: NodeRecyclerHolder)

    fun onViewAttachedToWindow(context: Context, nodeRecyclerHolder: NodeRecyclerHolder)

    fun onViewDetachedFromWindow(context: Context, nodeRecyclerHolder: NodeRecyclerHolder)

}
@file:Suppress("unused")

package com.herry.libs.nodeview.recycler

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodePosition
import kotlin.reflect.full.safeCast

@Suppress("MemberVisibilityCanBePrivate")
interface NodeRecyclerForm {

    companion object {
        fun getNodePosition(holder: NodeHolder): NodePosition? =
            getAdapter(holder)?.getNodePosition(holder.position?.let { it() } ?: 0)

        fun <H : NodeHolder, M : Any> getBindNode(
            form: NodeForm<H, M>,
            holder: NodeHolder
        ): Node<M>? = getAdapter(holder)?.getBindNode(holder.position?.let { it() } ?: 0)?.let {
            if (form.mClass.isInstance(it.model)) {
                @Suppress("UNCHECKED_CAST")
                it as Node<M>
            } else {
                null
            }
        }

        fun <H : NodeHolder, M : Any> getBindModel(form: NodeForm<H, M>, holder: NodeHolder): M? =
            getAdapter(holder)?.getBindNode(holder.position?.let { it() } ?: 0)?.let {
                form.mClass.safeCast(it.model)
            }

        fun getAdapter(holder: NodeHolder): NodeRecyclerAdapter? =
            (holder.view.parent as? RecyclerView)?.adapter as? NodeRecyclerAdapter
    }

    fun onViewRecycled(context: Context, holder: NodeHolder)

    fun onViewAttachedToWindow(context: Context, holder: NodeHolder)

    fun onViewDetachedFromWindow(context: Context, holder: NodeHolder)

}
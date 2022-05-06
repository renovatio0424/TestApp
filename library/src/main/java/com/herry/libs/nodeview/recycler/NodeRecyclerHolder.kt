package com.herry.libs.nodeview.recycler

import androidx.recyclerview.widget.RecyclerView
import com.herry.libs.nodeview.NodeHolder

open class NodeRecyclerHolder(val holder: NodeHolder) : RecyclerView.ViewHolder(holder.view) {
    init {
        holder.position = { bindingAdapterPosition }
    }
}
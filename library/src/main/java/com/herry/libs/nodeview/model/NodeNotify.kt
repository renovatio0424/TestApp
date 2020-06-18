package com.herry.libs.nodeview.model


internal interface NodeNotify {
    fun nodeSetChanged()
    fun nodeMoved(from: Int, to: Int)
    fun nodeChanged(position: Int, count: Int)
    fun nodeInserted(position: Int, count: Int)
    fun nodeRemoved(position: Int, count: Int)
    fun nodeEndTransition()
}
package com.herry.libs.nodeview.model


open class NodeModel: INodeModel {

    private var getNode: (() -> Node<*>)? = null

    override fun setOnGetNode(function: (() -> Node<*>)?) {
        getNode = function
    }

    override fun getNode(): Node<*>? {
        return getNode?.invoke()
    }
}
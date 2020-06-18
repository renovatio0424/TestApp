package com.herry.libs.nodeview.model


open class NodeModelGroup(override val id: Any? = null, isExpansion: Boolean = true): INodeModelGroup {
    override var isExpansion: Boolean = isExpansion
        set(value) {
            if (field != value) {
                field = value
                @Suppress("UNCHECKED_CAST")
                changedExpansion?.invoke(this@NodeModelGroup)
            }
        }

    private var getNode: (() -> Node<*>)? = null
    private var changedExpansion: ((model: INodeModelGroup) -> Unit)? = null

    override fun setOnChangedExpansion(function: ((model: INodeModelGroup) -> Unit)?) {
        changedExpansion = function
    }

    override fun setOnGetNode(function: (() -> Node<*>)?) {
        getNode = function
    }

    override fun getNode(): Node<*>? {
        return getNode?.invoke()
    }
}
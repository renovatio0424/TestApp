package com.herry.libs.nodeview.model



interface INodeModelGroup: INodeModel {
    val id: Any?

    var isExpansion: Boolean

    fun setOnChangedExpansion(function: ((model: INodeModelGroup) -> Unit)?)
}
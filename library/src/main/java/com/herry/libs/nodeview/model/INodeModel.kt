package com.herry.libs.nodeview.model

interface INodeModel {
    fun setOnGetNode(function: (() -> Node<*>)?)

    fun getNode(): Node<*>?
}
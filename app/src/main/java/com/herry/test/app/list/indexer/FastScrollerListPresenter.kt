package com.herry.test.app.list.indexer

import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodeHelper
import com.herry.libs.nodeview.model.NodeModelGroup

class FastScrollerListPresenter : FastScrollerListContract.Presenter() {

    private val nodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()

    override fun onAttach(view: FastScrollerListContract.View) {
        super.onAttach(view)

        view.root.beginTransition()
        NodeHelper.addNode(view.root, nodes)
        view.root.endTransition()
    }

    override fun onLaunch(view: FastScrollerListContract.View, recreated: Boolean) {
        if (recreated) {
            return
        }

        loadList()
    }

    private fun loadList() {
        view?.getViewContext() ?: return

        this.nodes.beginTransition()

        val nodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()

        var c = 'A'
        while (c <= 'Z') {
            NodeHelper.addModel(nodes, FastScrollerListContract.ListItemData("$c"))
            ++c
        }

        c = 'a'
        while (c <= 'z') {
            NodeHelper.addModel(nodes, FastScrollerListContract.ListItemData("$c"))
            ++c
        }

        NodeHelper.upSert(this.nodes, nodes)

        this.nodes.endTransition()
    }
}
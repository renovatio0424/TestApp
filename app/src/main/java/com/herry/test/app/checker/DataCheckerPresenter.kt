package com.herry.test.app.checker

import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodeHelper
import com.herry.libs.nodeview.model.NodeModelGroup

/**
 * Created by herry.park on 2020/7/7
 **/
class DataCheckerPresenter : DataCheckerContract.Presenter() {
    private val nodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()

    override fun onAttach(view: DataCheckerContract.View) {
        super.onAttach(view)

        view.root.beginTransition()
        NodeHelper.addNode(view.root, nodes)
        view.root.endTransition()
    }

    override fun onLaunched(view: DataCheckerContract.View) {
        // sets list items
        setList()
    }

    private fun setList() {
        view?.getViewContext() ?: return

        this.nodes.beginTransition()

        val nodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()
        DataCheckerContract.ItemType.values().forEach {
            NodeHelper.addModel(nodes, it)
        }
        NodeHelper.upSert(this.nodes, nodes)
        this.nodes.endTransition()
    }

    override fun show(item: DataCheckerContract.ItemType) {
        view?.getViewContext() ?: return

        view?.onShow(item)
    }
}
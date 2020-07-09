package com.herry.test.app.intent.scheme

import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodeHelper
import com.herry.libs.nodeview.model.NodeModelGroup

/**
 * Created by herry.park on 2020/06/11.
 **/
class SchemePresenter : SchemeContract.Presenter() {
    private val nodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()

    override fun onAttach(view: SchemeContract.View) {
        super.onAttach(view)

        view.root.beginTransition()
        NodeHelper.addNode(view.root, nodes)
        view.root.endTransition()
    }

    override fun onLaunched(view: SchemeContract.View) {
        // sets list items
        setTestItems()
    }

    private fun setTestItems() {
        view?.getViewContext() ?: return

        this.nodes.beginTransition()

        val nodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()
        SchemeContract.SchemeItemType.values().forEach {
            NodeHelper.addModel(nodes, it)
        }
        NodeHelper.upSert(this.nodes, nodes)
        this.nodes.endTransition()
    }

    override fun gotoScheme(type: SchemeContract.SchemeItemType) {
        view?.getViewContext() ?: return

        view?.onGotoScheme(type.url)
    }
}
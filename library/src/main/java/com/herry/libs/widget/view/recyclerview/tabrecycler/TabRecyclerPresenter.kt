package com.herry.libs.widget.view.recyclerview.tabrecycler

import android.os.Parcelable
import com.herry.libs.nodeview.model.NodeHelper

abstract class TabRecyclerPresenter : TabRecyclerContract.Presenter() {

    protected var view: TabRecyclerContract.View? = null
        private set
    private var launched = false
    private var saveInstanceState: Parcelable? = null

    protected val nodes = NodeHelper.createNodeGroup()

    override fun onAttach(view: TabRecyclerContract.View) {
        this.view = view
        view.root.let {
            it.beginTransition()
            it.clearChild()
            NodeHelper.addNode(it, nodes)
            it.endTransition()
            if (!launched) {
                launched = true
                onLaunch()
            } else {
                onResume()
            }
        }
        view.onAttached(saveInstanceState)
        saveInstanceState = null
    }

    override fun onDetach() {
        this.view?.let { view ->
            saveInstanceState = view.onDetached()
            view.root.beginTransition()
            view.root.clearChild()
            view.root.endTransition()
        }
        this.view = null
    }

    fun init() {
        launched = if (view != null) {
            onLaunch()
            true
        } else {
            nodes.clearChild()
            false
        }
    }

    override fun setCurrentPresent() {
        view?.onNotifyScrollState()
    }

    protected fun isEmpty(): Boolean = this.nodes.getChildCount() == 0
}
package com.herry.libs.nodeview.model

import android.util.Log


class NodeRoot internal constructor(private val notify: NodeNotify, private val log: Boolean = false) : Node<NodeModelGroup>(NodeModelGroup()) {

    private var nextNodeId = 0L

    private var isTransition = false
    private var isNodeSetChanged = false

    private var param: NodeNotifyParam? = null

    internal fun nextNodeId(): Long {
        nextNodeId++
        if (nextNodeId == Long.MAX_VALUE) {
            nextNodeId = 1L
        }
        return nextNodeId
    }

    @Suppress("UNUSED_PARAMETER")
    override var parent: Node<*>?
        get() = null
        set(value) {}

    override fun getRoot(): NodeRoot? = this

    override fun beginTransition() {
        isTransition = true
        isNodeSetChanged = getViewCount() <= 0
        if (log) {
            Log.d("node_ui", "beginTransition isNodeSetChanged : $isNodeSetChanged")
        }
    }

    override fun endTransition() {
        if (log) {
            Log.d("node_ui", "endTransition isTransition : $isTransition isNodeSetChanged : $isNodeSetChanged")
        }

        if (!isTransition || isNodeSetChanged) {
            traversals()
            this.param = null
            notify.nodeSetChanged()
            isNodeSetChanged = false
            isTransition = false
        } else {
            this.param?.let {
                applyTo(it)
            }
            this.param = null
            isTransition = false
        }
        notify.nodeEndTransition()
    }

    override fun notifyFromChild(param: NodeNotifyParam, then: (() -> Unit)?) {
        val success = notify(param, then)
        if (!success) {
            notify(param, null)
        }
    }

    override fun notify(param: NodeNotifyParam, then: (() -> Unit)?): Boolean {
        if (log && then != null) {
            Log.d("node_ui", "notify param $param")
        }

        if (isNodeSetChanged) {
            if (log) {
                Log.d("node_ui", "notify isNodeSetChanged : TRUE")
            }
            then?.let { it() }
            return true
        }

        if (isTransition) {
            if (this.param != null) {
                val composeParam = this.param!!.compose(param)
                if (log) {
                    Log.d("node_ui", "notify isTransition : $isTransition this.param : ${this.param} composeParam : $composeParam ")
                }

                return if (composeParam != null) {
                    this.param = composeParam
                    then?.let { it() }
                    true
                } else {
                    applyTo(this.param!!)
                    then?.let { it() }
                    this.param = null
                    false
                }
            } else {
                if (log) {
                    Log.d("node_ui", "notify isTransition : $isTransition this.param : null")
                }
                this.param = param
                then?.let { it() }
                return true
            }
        } else {
            if (log) {
                Log.d("node_ui", "notify $isTransition ${getViewCount()}")
            }
            if (getViewCount() <= 0) {
                then?.let { it() }
                traversals()
                this.param = null
                notify.nodeSetChanged()
            } else {
                then?.let { it() }
                applyTo(param)
            }
            return true
        }
    }

    private fun applyTo(param: NodeNotifyParam) {
        when (param.state) {
            NodeNotifyParam.STATE.CHANGE -> {
                if (log) {
                    Log.d("node_ui", "applyTo CHANGED : POS = ${param.position}, CNT = ${param.count}")
                }
                notify.nodeChanged(param.position, param.count)
            }
            NodeNotifyParam.STATE.INSERT -> {
                if (log) {
                    Log.d("node_ui", "applyTo INSERTED : POS = ${param.position}, CNT = ${param.count}")
                }
                traversals()
                notify.nodeInserted(param.position, param.count)
            }
            NodeNotifyParam.STATE.REMOVE -> {
                if (log) {
                    Log.d("node_ui", "applyTo REMOVED : POS = ${param.position}, CNT = ${param.count}")
                }
                traversals()
                notify.nodeRemoved(param.position, param.count)
            }
        }
    }

    override fun traversals(): Int {
        return nodeChildren.traversals()
    }
}
package com.herry.libs.nodeview.model


internal class NodeNotifyParam(var state: STATE, var position: Int, var count: Int) {

    enum class STATE {
        CHANGE,
        INSERT,
        REMOVE
    }

    internal fun compose(param: NodeNotifyParam): NodeNotifyParam? {
        if (state != param.state) {
            return null
        }

        return when (state) {
            STATE.CHANGE -> composeChange(param)
            STATE.INSERT -> composeInsert(param)
            STATE.REMOVE -> composeRemove(param)
        }
    }

    private fun composeChange(param: NodeNotifyParam): NodeNotifyParam? {
        if ((position in param.position..(param.position + param.count))
            || (param.position in position..(position + count))
        ) {
            val maxPosition = if (position + count > param.position + param.count)
                position + count
            else
                param.position + param.count

            return if (position < param.position) {
                NodeNotifyParam(STATE.CHANGE, position, maxPosition - position)
            } else {
                NodeNotifyParam(STATE.CHANGE, param.position, maxPosition - param.position)
            }
        }
        return null
    }

    private fun composeInsert(param: NodeNotifyParam): NodeNotifyParam? {
        if (param.position in position..(position + count)) {
            return NodeNotifyParam(STATE.INSERT, position, count + param.count)
        }
        return null
    }

    private fun composeRemove(param: NodeNotifyParam): NodeNotifyParam? {
        if (position in param.position..(param.position + param.count)) {
            return NodeNotifyParam(STATE.REMOVE, param.position, count + param.count)
        }
        return null
    }

    override fun toString(): String {
        return "state : $state position : $position count : $count"
    }
}
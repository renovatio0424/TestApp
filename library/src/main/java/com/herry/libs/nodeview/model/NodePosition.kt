package com.herry.libs.nodeview.model


class NodePosition(val position: IntArray) {
    companion object {
        fun compose(pos: Int, pPos: NodePosition?): NodePosition {
            if(pPos != null) {
                val newPPos = IntArray(pPos.position.size + 1)
                newPPos[0] = pos
                for(i in 1..pPos.position.size) {
                    newPPos[i] = pPos.position[i -1]
                }
                return NodePosition(newPPos)
            }
            return NodePosition(intArrayOf(pos))
        }
    }

    fun getViewPosition(): Int {
        var viewPosition = 0
        for(inPosition in position) {
            viewPosition += inPosition
        }
        return viewPosition
    }

    fun getPosition(): Int {
        if(position.isNotEmpty()) {
            return position[position.size - 1]
        }
        return -1
    }

    fun getParentPosition(): NodePosition? {
        if(position.size > 1) {
            val parentPos = IntArray(position.size - 1)
            for(i in 0 until parentPos.size) {
                parentPos[i] = position[i]
            }
            return NodePosition(parentPos)
        }
        return null
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for(pos in position) {
            sb.append("[$pos]")
        }
        return sb.toString()
    }
}
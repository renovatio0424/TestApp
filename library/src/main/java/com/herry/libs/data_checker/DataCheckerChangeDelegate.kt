package com.herry.libs.data_checker


class DataCheckerChangeDelegate(private val checker: DataCheckerChange?) : DataCheckerChange {
    private var change = false
    private val listener: MutableList<DataCheckerChange.OnDataCheckerChangedListener> = mutableListOf()

    override fun addOnCheckerListener(listener: DataCheckerChange.OnDataCheckerChangedListener) {
        this.listener.add(listener)
    }

    override fun removeOnCheckerListener(listener: DataCheckerChange.OnDataCheckerChangedListener) {
        val iterator = this.listener.iterator()
        while (iterator.hasNext()) {
            if (iterator.next() == listener) {
                iterator.remove()
            }
        }
    }

    override var isChanged: Boolean
        get() = change
        set(value) { setChanged(value, false) }

    fun setChanged(change: Boolean, notify: Boolean) {
        if (notify || this.change != change) {
            this.change = change
            notifyChecker()
        }
    }

    private fun notifyChecker() {
        for (listener in this.listener) {
            listener.onCheckerChanged(checker ?: this)
        }
    }

}
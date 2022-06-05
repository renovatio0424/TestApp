package com.herry.libs.data_checker

class DataCheckerMandatoryDelegate(private val checker: DataCheckerMandatory?) : DataCheckerMandatory {

    private var isMandatory = false
    private var isChanged = false

    private val listener: MutableList<DataCheckerMandatory.OnDataCheckerChangedListener> = mutableListOf()

    override fun isChanged(): Boolean = isChanged

    override fun isMandatory(): Boolean = isMandatory

    override fun addOnCheckerListener(listener: DataCheckerMandatory.OnDataCheckerChangedListener) {
        this.listener.add(listener)
    }

    override fun removeOnCheckerListener(listener: DataCheckerMandatory.OnDataCheckerChangedListener) {
        val iterator = this.listener.iterator()
        while (iterator.hasNext()) {
            if (iterator.next() == listener) {
                iterator.remove()
            }
        }
    }

    fun setMandatory(mandatory: Boolean, change: Boolean) {
        setMandatory(mandatory, change, false)
    }

    fun setMandatory(mandatory: Boolean, change: Boolean, notify: Boolean) {
        if (notify || isMandatory != mandatory || isChanged != change) {
            isMandatory = mandatory
            isChanged = change
            notifyChecker()
        }
    }

    private fun notifyChecker() {
        for (listener in this.listener) {
            listener.onCheckerChanged(checker ?: this)
        }
    }
}
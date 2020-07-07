package com.herry.libs.data_checker


@Suppress("unused")
open class DataCheckerChangeData<T> : DataCheckerChange {
    var base: T? = null
        private set
    var data: T? = null
        private set
    private val checker: DataCheckerChangeDelegate by lazy { DataCheckerChangeDelegate(this) }
    private var listener: OnDataCheckerChangedListener<T>? = null

    constructor() {
        setBase(null)
    }

    constructor(baseObj: T) {
        setBase(baseObj)
    }

    fun setBase(data: T?) {
        base = data
        setData(data, true)
    }

    fun setData(data: T) {
        setData(data, false)
    }

    private fun setData(data: T?, notify: Boolean) {
        if (!equals<T?, T?>(this.data, data) || notify) {
            this.data = data
            checker.isChanged = isChangedCheck(base, this.data)
            listener?.onChangedData(this.data)
        }
    }

    fun setOnChangedListener(listener: OnDataCheckerChangedListener<T>?) {
        this.listener = listener
    }

    open fun isChangedCheck(base: T?, data: T?): Boolean {
        return !equals<T, T>(base, data)
    }

    override var isChanged: Boolean
        get() = checker.isChanged
        set(value) {checker.isChanged = value}

    override fun addOnCheckerListener(listener: DataCheckerChange.OnDataCheckerChangedListener) {
        checker.addOnCheckerListener(listener)
    }

    override fun removeOnCheckerListener(listener: DataCheckerChange.OnDataCheckerChangedListener) {
        checker.removeOnCheckerListener(listener)
    }

    companion object {
        fun <T1, T2> equals(t1: T1?, t2: T2?): Boolean {
            return (t1 == null && t2 == null) || (t1 != null && t1 == t2)
        }
    }
}
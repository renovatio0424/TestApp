package com.herry.libs.data_checker

@Suppress("unused")
open class DataCheckerChangeData<T> : DataCheckerChange {
    private var base: T? = null
    private var data: T? = null
    private val checker: DataCheckerChangeDelegate by lazy { DataCheckerChangeDelegate(this) }
    private var listener: OnDataCheckerChangedListener<T>? = null

    constructor() {
        setBase(null)
    }

    constructor(base: T) {
        setBase(base)
    }

    fun getBase(): T? = this.base

    fun setBase(data: T?) {
        base = data
        setData(data, true)
    }

    fun getData(): T? = this.data

    fun setData(data: T?) {
        setData(data, false)
    }

    private fun setData(data: T?, notify: Boolean) {
        if (!equals<T?, T?>(this.data, data) || notify) {
            this.data = data
            checker.setChanged(isChangedCheck(base, this.data))
            listener?.onChangedData(this.data)
        }
    }

    fun setOnChangedListener(listener: OnDataCheckerChangedListener<T>?) {
        this.listener = listener
    }

    open fun isChangedCheck(base: T?, data: T?): Boolean {
        return !equals<T, T>(base, data)
    }

    override fun isChanged(): Boolean = checker.isChanged()

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
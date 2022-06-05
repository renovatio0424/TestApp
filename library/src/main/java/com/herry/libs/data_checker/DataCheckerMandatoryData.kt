package com.herry.libs.data_checker


@Suppress("MemberVisibilityCanBePrivate", "unused")
open class DataCheckerMandatoryData<T> : DataCheckerMandatory {
    private var base: T? = null
    private var data: T? = null

    private val checker by lazy { DataCheckerMandatoryDelegate(this) }
    private var listener: OnDataCheckerChangedListener<T>? = null

    constructor() {
        setBase(null)
    }

    constructor(base: T) {
        setBase(base)
    }

    fun getBase(): T? = this.base

    fun setBase(data: T?) {
        this.base = data
        setData(data, true)
    }

    fun getData(): T? = this.data

    fun setData(data: T) {
        setData(data, false)
    }

    private fun setData(data: T?, notify: Boolean) {
        if (!DataCheckerChangeData.equals(this.data, data) || notify) {
            this.data = data
            checker.setMandatory(isMandatoryCheck(), isChangeCheck(this.base, this.data))
            listener?.onChangedData(this.data)
        }
    }

    fun setOnChangedListener(listener: OnDataCheckerChangedListener<T>?) {
        this.listener = listener
    }

    open fun isMandatoryCheck(): Boolean = this.data != null

    open fun isChangeCheck(base: T?, data: T?): Boolean = !DataCheckerChangeData.equals(base, data)

    override fun isChanged(): Boolean = checker.isChanged()

    override fun isMandatory(): Boolean = checker.isMandatory()

    override fun addOnCheckerListener(listener: DataCheckerMandatory.OnDataCheckerChangedListener) {
        checker.addOnCheckerListener(listener)
    }

    override fun removeOnCheckerListener(listener: DataCheckerMandatory.OnDataCheckerChangedListener) {
        checker.removeOnCheckerListener(listener)
    }
}
package com.herry.libs.data_checker


@Suppress("MemberVisibilityCanBePrivate", "unused")
open class DataCheckerMandatoryData<T> : DataCheckerMandatory {
    var base: T? = null
        private set
    var data: T? = null
        private set
    private val checker by lazy { DataCheckerMandatoryDelegate(this) }
    private var listener: OnDataCheckerChangedListener<T>? = null

    constructor() {
        setBase(null)
    }

    constructor(base: T) {
        setBase(base)
    }

    fun setBase(data: T?) {
        base = data
        setData(data, true)
    }

    fun setData(data: T) {
        setData(data, false)
    }

    private fun setData(data: T?, notify: Boolean) {
        if (!DataCheckerChangeData.equals(this.data, data) || notify) {
            this.data = data
            checker.setMandatory(isMandatoryCheck, isChangeCheck(base, this.data))
            listener?.onChangedData(this.data)
        }
    }

    fun setOnChangedListener(listener: OnDataCheckerChangedListener<T>?) {
        this.listener = listener
    }

    open val isMandatoryCheck: Boolean
        get() = data != null

    open fun isChangeCheck(base: T?, data: T?): Boolean {
        return !DataCheckerChangeData.equals(base, data)
    }

    override val isChanged: Boolean
        get() = checker.isChanged

    override val isMandatory: Boolean
        get() = checker.isMandatory

    override fun addOnCheckerListener(listener: DataCheckerMandatory.OnDataCheckerChangedListener) {
        checker.addOnCheckerListener(listener)
    }

    override fun removeOnCheckerListener(listener: DataCheckerMandatory.OnDataCheckerChangedListener) {
        checker.removeOnCheckerListener(listener)
    }
}
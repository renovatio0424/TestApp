package com.herry.libs.data_checker

@Suppress("unused", "MemberVisibilityCanBePrivate")
class DataCheckerSet(checker: DataChecker<*>?) {
    private val changeChecker: DataCheckerChangeDelegate
    private val mandatoryChecker: DataCheckerMandatoryDelegate
    private val hashMap = HashMap<DataChecker<*>, CheckerValue>()
    private var changedCount = 0
    private var mandatoryCount = 0
    private val onChangedListener = object : DataCheckerChange.OnDataCheckerChangedListener {
        override fun onCheckerChanged(checker: DataCheckerChange) {
            applyCheckerValue(checker, createCheckerValue(checker))
        }
    }
    private val onMandatoryListener = object : DataCheckerMandatory.OnDataCheckerChangedListener {
        override fun onCheckerChanged(checker: DataCheckerMandatory) {
            applyCheckerValue(checker, createCheckerValue(checker))
        }
    }

    fun addOnChangedListener(listener: DataCheckerChange.OnDataCheckerChangedListener) {
        changeChecker.addOnCheckerListener(listener)
    }

    fun removeOnChangedListener(listener: DataCheckerChange.OnDataCheckerChangedListener) {
        changeChecker.removeOnCheckerListener(listener)
    }

    fun addOnMandatoryListener(listener: DataCheckerMandatory.OnDataCheckerChangedListener) {
        mandatoryChecker.addOnCheckerListener(listener)
    }

    fun removeOnMandatoryListener(listener: DataCheckerMandatory.OnDataCheckerChangedListener) {
        mandatoryChecker.removeOnCheckerListener(listener)
    }

    fun setCheckerList(vararg checkers: DataChecker<*>) {
        setCheckerList(mutableListOf(*checkers))
    }

    fun setCheckerList(list: List<DataChecker<*>?>) {
        for ((checker) in hashMap) {
            if (checker is DataCheckerChange) {
                checker.removeOnCheckerListener(onChangedListener)
            } else if (checker is DataCheckerMandatory) {
                checker.removeOnCheckerListener(onMandatoryListener)
            }
        }
        hashMap.clear()
        var changeCount = 0
        var mandatoryCount = 0
        for (checker in list) {
            var value: CheckerValue? = null

            when (checker) {
                is DataCheckerChange -> {
                    checker.addOnCheckerListener(onChangedListener)
                    value = createCheckerValue(checker)
                    hashMap[checker] = value
                }
                is DataCheckerMandatory -> {
                    checker.addOnCheckerListener(onMandatoryListener)
                    value = createCheckerValue(checker)
                    hashMap[checker] = value
                }
            }

            if (value != null) {
                changeCount += if (value.isChanged) 1 else 0
                mandatoryCount += if (value.isMandatory) 1 else 0
            }
        }

        this.changedCount = changeCount
        this.mandatoryCount = mandatoryCount

        val isChange = this.changedCount > 0
        val isMandatory = this.mandatoryCount == hashMap.size

        changeChecker.setChanged(isChange, true)
        mandatoryChecker.setMandatory(isMandatory, isChange, true)
    }

    fun addChecker(checker: DataChecker<*>?) {
        if (null == checker) {
            return
        }
        val value: CheckerValue
        when (checker) {
            is DataCheckerChange -> {
                checker.addOnCheckerListener(onChangedListener)
                value = createCheckerValue(checker)
                hashMap[checker] = value
            }
            is DataCheckerMandatory -> {
                checker.addOnCheckerListener(onMandatoryListener)
                value = createCheckerValue(checker)
                hashMap[checker] = value
            }
            else -> {
                return
            }
        }

        changedCount += if (value.isChanged) 1 else 0
        mandatoryCount += if (value.isMandatory) 1 else 0

        val isChange = changedCount > 0
        val isMandatory = mandatoryCount == hashMap.size

        changeChecker.setChanged(isChange, true)
        mandatoryChecker.setMandatory(isMandatory, isChange, true)
    }

    val checkers: List<DataChecker<*>>
        get() = ArrayList(hashMap.keys)

    fun isChanged(): Boolean = changeChecker.isChanged

    fun isMandatory(): Boolean = mandatoryChecker.isMandatory

    private fun createCheckerValue(checker: DataCheckerChange): CheckerValue {
        return CheckerValue(true, checker.isChanged)
    }

    private fun createCheckerValue(checker: DataCheckerMandatory): CheckerValue {
        return CheckerValue(checker.isMandatory, checker.isChanged)
    }

    private fun applyCheckerValue(checker: DataChecker<*>?, value: CheckerValue?) {
        if (checker == null || value == null) {
            return
        }

        val checkerValue = hashMap[checker] ?: return
        if (checkerValue == value) {
            return
        }

        hashMap[checker] = value
        var changeCount = changedCount
        var mandatoryCount = mandatoryCount

        if (checkerValue.isChanged != value.isChanged) {
            changeCount += if (value.isChanged) 1 else -1
        }

        if (checkerValue.isMandatory != value.isMandatory) {
            mandatoryCount += if (value.isMandatory) 1 else -1
        }

        val oldChange = this.changedCount > 0
        val newChange = changeCount > 0
        val oldMandatory = this.mandatoryCount == hashMap.size
        val newMandatory = mandatoryCount == hashMap.size

        this.changedCount = changeCount
        this.mandatoryCount = mandatoryCount

        if (oldChange != newChange) {
            changeChecker.isChanged = newChange
            mandatoryChecker.setMandatory(newMandatory, newChange)
        } else {
            if (oldMandatory != newMandatory) {
                mandatoryChecker.setMandatory(newMandatory, newChange)
            }
        }
    }

    private inner class CheckerValue internal constructor(
            var isMandatory: Boolean,
            var isChanged: Boolean
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false
            val value = other as CheckerValue
            return if (isMandatory != value.isMandatory) false else isChanged == value.isChanged
        }

        override fun hashCode(): Int {
            var result = isMandatory.hashCode()
            result = 31 * result + isChanged.hashCode()
            return result
        }
    }

    init {
        val checkerChange: DataCheckerChange? = if (checker is DataCheckerChange) checker else null
        val checkerMandatory: DataCheckerMandatory? = if (checker is DataCheckerMandatory) checker else null

        changeChecker = DataCheckerChangeDelegate(checkerChange)
        mandatoryChecker = DataCheckerMandatoryDelegate(checkerMandatory)
    }
}
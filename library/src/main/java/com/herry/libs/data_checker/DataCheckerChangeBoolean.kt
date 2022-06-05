package com.herry.libs.data_checker

@Suppress("unused")
class DataCheckerChangeBoolean : DataCheckerChangeData<Boolean>(base = false) {
    override fun isChangedCheck(base: Boolean?, data: Boolean?): Boolean {
        return base ?: false != data ?: false
    }
}
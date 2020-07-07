package com.herry.libs.data_checker


@Suppress("unused")
class DataCheckerMandatoryBoolean : DataCheckerMandatoryData<Boolean>() {
    override val isMandatoryCheck: Boolean
        get() = data != null && data as Boolean

    override fun isChangeCheck(base: Boolean?, data: Boolean?): Boolean {
        return base ?: false != data ?: false
    }
}
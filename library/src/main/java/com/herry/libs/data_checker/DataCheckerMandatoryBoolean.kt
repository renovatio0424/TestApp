package com.herry.libs.data_checker


@Suppress("unused")
class DataCheckerMandatoryBoolean : DataCheckerMandatoryData<Boolean>() {
    override fun isMandatoryCheck(): Boolean = getData() ?: false

    override fun isChangeCheck(base: Boolean?, data: Boolean?): Boolean = (base ?: false) != (data ?: false)
}
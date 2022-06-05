package com.herry.libs.data_checker

@Suppress("unused")
class DataCheckerMandatoryString : DataCheckerMandatoryData<String>() {
    override fun isMandatoryCheck(): Boolean = !getData().isNullOrBlank()
}
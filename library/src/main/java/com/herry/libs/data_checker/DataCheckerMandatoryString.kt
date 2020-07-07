package com.herry.libs.data_checker

@Suppress("unused")
class DataCheckerMandatoryString : DataCheckerMandatoryData<String>() {
    override val isMandatoryCheck: Boolean
        get() = !data.isNullOrBlank()
}
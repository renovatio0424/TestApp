package com.herry.libs.data_checker

interface DataCheckerMandatory : DataChecker<DataCheckerMandatory.OnDataCheckerChangedListener> {
    interface OnDataCheckerChangedListener : OnDataCheckerListener<DataCheckerMandatory>

    fun isChanged(): Boolean
    fun isMandatory(): Boolean
}
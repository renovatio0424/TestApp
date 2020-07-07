package com.herry.libs.data_checker

interface DataCheckerMandatory : DataChecker<DataCheckerMandatory.OnDataCheckerChangedListener> {
    interface OnDataCheckerChangedListener : OnDataCheckerListener<DataCheckerMandatory>

    val isChanged: Boolean
    val isMandatory: Boolean
}
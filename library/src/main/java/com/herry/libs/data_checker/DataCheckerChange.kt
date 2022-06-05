package com.herry.libs.data_checker

interface DataCheckerChange : DataChecker<DataCheckerChange.OnDataCheckerChangedListener> {
    interface OnDataCheckerChangedListener : OnDataCheckerListener<DataCheckerChange>

    fun isChanged(): Boolean
}
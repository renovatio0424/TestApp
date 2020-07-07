package com.herry.libs.data_checker

interface OnDataCheckerChangedListener<T> {
    fun onChangedData(data: T?)
}
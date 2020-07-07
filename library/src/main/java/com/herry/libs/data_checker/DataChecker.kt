package com.herry.libs.data_checker

interface DataChecker<T : OnDataCheckerListener<*>> {
    fun addOnCheckerListener(listener: T)
    fun removeOnCheckerListener(listener: T)
}
package com.herry.libs.data_checker

interface OnDataCheckerListener<T> {
    fun onCheckerChanged(checker: T)
}
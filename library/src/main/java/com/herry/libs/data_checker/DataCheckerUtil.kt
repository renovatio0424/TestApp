package com.herry.libs.data_checker

/**
 * Created by herry.park on 2020/07/06.
 **/
@Suppress("unused")
object DataCheckerUtil {

    fun <T> getData(checkerData: DataCheckerChangeData<T>, default: T) : T {
        return checkerData.getData() ?: return default
    }
}
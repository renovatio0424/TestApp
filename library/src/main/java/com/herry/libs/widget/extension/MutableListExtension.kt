package com.herry.libs.widget.extension

import java.util.*

/**
 * Created by herry.park on 2020/09/03.
 **/
fun <T> MutableList<T>.forEachStep(step: Int, action: (steps: MutableList<T>) -> Unit) {
    if (step > 0) {
        var index = 0
        while (index < size) {
            if (index + step <= size) {
                val steps: MutableList<T> = ArrayList()
                for (subIndex in index until index + step) {
                    steps.add(this[subIndex])
                }
                action(steps)
            }
            index += step
        }
    }
}

//fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
//    val tmp = this[index1] // 'this' corresponds to the list
//    this[index1] = this[index2]
//    this[index2] = tmp
//}
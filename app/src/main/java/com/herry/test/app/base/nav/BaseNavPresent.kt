package com.herry.test.app.base.nav

import android.os.Handler
import android.os.Looper
import com.herry.test.app.base.mvp.BasePresent

abstract class BaseNavPresent<V>: BasePresent<V>() {

    private var isNavTransition = false

    private val launchFunctions = mutableListOf<() -> Unit>()

    fun navTransitionStart() {
        isNavTransition = true
    }

    open fun navTransitionEnd() {
        Handler(Looper.getMainLooper()).post {
            if (isNavTransition) {
                isNavTransition = false
                val iterator: MutableIterator<() -> Unit> = launchFunctions.iterator()
                while (iterator.hasNext()) {
                    iterator.next()()
                    iterator.remove()
                }
            }
        }
    }

    override fun launched(function: () -> Unit) {
        if (view != null) {
            if (isNavTransition) {
                launchFunctions.add(function)
            } else {
                function.invoke()
            }
        }
    }
}
package com.herry.libs.widget.extension

import android.os.SystemClock
import android.view.View

/**
 * Created by herry.park on 2020/06/17.
 **/
class OnProtectClick(val listener: ((v: View) -> Unit)): View.OnClickListener {

    companion object {
        const val protectTimeMs = 500L
        var lastClickTimeMs = 0L
    }

    private fun isClickable(): Boolean {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        if(elapsedRealtime - lastClickTimeMs < protectTimeMs) {
            return false
        }
        lastClickTimeMs = elapsedRealtime
        return true
    }

    override fun onClick(v: View?) {
        v?.let {
            if(isClickable() ) {
                listener(it)
            }
        }

    }
}

fun View.setOnProtectClickListener(l: ((v: View) -> Unit)?) {
    if(l != null) {
        setOnClickListener(OnProtectClick(l))
    } else {
        setOnClickListener(null)
    }
}
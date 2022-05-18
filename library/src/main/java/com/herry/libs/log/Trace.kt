package com.herry.libs.log

import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

@Suppress("unused")
object Trace {
    private val TAG = AtomicReference<String>()

    private val DEBUG = AtomicBoolean(false)

    fun setTAG(tag: String?) {
        TAG.set(tag ?: "")
    }

    private fun getTAG(): String? {
        return TAG.get()
    }

    fun setDebug(debug: Boolean) {
        DEBUG.set(debug)
    }

    private fun isDebug(): Boolean {
        return DEBUG.get()
    }

    fun e(tag: String? = getTAG(), log: String?, throwable: Throwable? = null) {
        Log.e(tag, log, throwable)
    }

    fun d(tag: String? = getTAG(), log: String?) {
        if (isDebug()) {
            Log.d(tag, log!!)
        }
    }

    fun w(tag: String? = getTAG(), log: String?) {
        if (isDebug()) {
            Log.w(tag, log!!)
        }
    }

    fun i(tag: String? = getTAG(), log: String?) {
        if (isDebug()) {
            Log.i(tag, log!!)
        }
    }

    fun v(tag: String? = getTAG(), log: String?) {
        if (isDebug()) {
            Log.v(tag, log!!)
        }
    }
}
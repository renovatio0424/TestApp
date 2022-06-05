package com.herry.libs.widget.view.click

import android.os.SystemClock
import android.view.View

/**
 * OnClickListener 중복 클릭 처리
 */
abstract class OnProtectClickListener : View.OnClickListener {
    companion object {
        private const val DEFAULT_CLICK_INTERVAL_TIME_MS = 500L
        private var lastClickTimeMs = 0L // sets value to static
    }
    // view interval time
    private val clickIntervalTimeMs: Long

    abstract fun onSingleClick(v: View)

    constructor() {
        clickIntervalTimeMs = DEFAULT_CLICK_INTERVAL_TIME_MS
    }

    constructor(clickIntervalTimeMs: Long) {
        if (0 >= clickIntervalTimeMs) {
            this.clickIntervalTimeMs = DEFAULT_CLICK_INTERVAL_TIME_MS
        } else {
            this.clickIntervalTimeMs = clickIntervalTimeMs
        }
    }

    /**
     * Checks clickable view or not.
     * @return true if view is clickable or false if other view is clicked.
     */
    fun updateLastClickTime(): Boolean {
        synchronized(this) {
            val elapsedRealtime = SystemClock.elapsedRealtime()
            if (elapsedRealtime - lastClickTimeMs < clickIntervalTimeMs) {
                return false
            }
            lastClickTimeMs = elapsedRealtime
            return true
        }
    }

    override fun onClick(view: View) {
        if (null == view) return
        if (updateLastClickTime()) {
            onSingleClick(view)
        }
    }

}
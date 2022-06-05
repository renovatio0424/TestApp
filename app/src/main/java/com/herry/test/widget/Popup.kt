package com.herry.test.widget

import android.content.Context
import androidx.annotation.StyleRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.herry.libs.widget.view.dialog.AppDialog
import com.herry.test.R

@Suppress("unused")
class Popup(
    context: Context?,
    @StyleRes style: Int = R.style.PopupTheme,
    @StyleRes dialogThemeResId: Int = 0
) : AppDialog(context, style, dialogThemeResId) {
    private var owner: LifecycleOwner? = null

    private var keepOnPause: Boolean = true

    fun setLifecycleOwner(owner: LifecycleOwner) {
        this.owner = owner
        owner.lifecycle.addObserver(PopupObserver(this))
    }

    fun setKeepOnPause(keep: Boolean) {
        owner ?: throw IllegalStateException("Must call setLifecycleOwner() function ")

        this.keepOnPause = keep
    }

    private class PopupObserver(val dialog: Popup) : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            if (dialog.isShowing() && !dialog.keepOnPause) {
                dialog.dismiss()
            }
        }
    }
}
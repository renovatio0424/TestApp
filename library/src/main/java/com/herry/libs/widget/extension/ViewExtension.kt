package com.herry.libs.widget.extension

import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.herry.libs.util.ViewUtil

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

fun ImageView.setImage(
    drawable: Drawable?,
    @ColorRes statefulColorSateResId: Int = 0,
    @ColorRes notStatefulColorSateResId: Int = 0
) {
    val context = this.context ?: return

    // set drawable to image view
    setImageDrawable(drawable)

    drawable ?: return

    // sets color sate list
    imageTintList = ViewUtil.getColorStateList(
        context, if (drawable.isStateful) statefulColorSateResId else notStatefulColorSateResId)
}

fun ImageView.setImage(
    @DrawableRes resId: Int,
    @ColorRes statefulColorSateResId: Int = 0,
    @ColorRes notStatefulColorSateResId: Int = 0
) {
    val context = this.context ?: return

    // gets drawable
    val drawable: Drawable? = ViewUtil.getDrawable(context, resId)

    setImage(drawable, statefulColorSateResId, notStatefulColorSateResId)
}
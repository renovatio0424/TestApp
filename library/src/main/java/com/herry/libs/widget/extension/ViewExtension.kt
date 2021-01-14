@file:Suppress("unused")

package com.herry.libs.widget.extension

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
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


fun View.setViewSize(width: Int, height: Int) {
    val params = this.layoutParams
    // set width
    if (width >= 0) {
        params.width = width
    } else if (ViewGroup.LayoutParams.MATCH_PARENT == width || ViewGroup.LayoutParams.WRAP_CONTENT == width) {
        params.width = width
    }

    // set height
    if (height >= 0) {
        params.height = height
    } else if (ViewGroup.LayoutParams.MATCH_PARENT == height || ViewGroup.LayoutParams.WRAP_CONTENT == height) {
        params.height = height
    }
    this.layoutParams = params
}

fun View.setViewWidth(width: Int) {
    val params = this.layoutParams
    // set width
    if (0 <= width) {
        params.width = width
    } else if (ViewGroup.LayoutParams.MATCH_PARENT == width || ViewGroup.LayoutParams.WRAP_CONTENT == width) {
        params.width = width
    }
    this.layoutParams = params
}

fun View.setViewHeight(height: Int) {
    val params: ViewGroup.LayoutParams = this.layoutParams

    if (0 <= height) {
        params.height = height
    } else if (ViewGroup.LayoutParams.MATCH_PARENT == height || ViewGroup.LayoutParams.WRAP_CONTENT == height) {
        params.height = height
    }

    this.layoutParams = params
}

fun View.setViewWeight(weight: Float) {
    val params = this.layoutParams
    if (params is LinearLayout.LayoutParams) {
        params.weight = weight
    } else {
        return
    }
    this.layoutParams = params
}

fun View.setViewMargin(left: Int, top: Int, right: Int, bottom: Int) {
    if (this.layoutParams is ViewGroup.MarginLayoutParams) {
        val params = this.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(left, top, right, bottom)
        this.layoutParams = params
    }
}

fun View.setViewMargin(margin: Int) {
    this.setViewMargin(margin, margin, margin, margin)
}

fun View.getViewMargin(): Rect {
    val margins = Rect()
    if (this.layoutParams is ViewGroup.MarginLayoutParams) {
        val params = this.layoutParams as ViewGroup.MarginLayoutParams
        margins.left = params.marginStart
        margins.top = params.topMargin
        margins.right = params.marginEnd
        margins.bottom = params.bottomMargin
    }
    return margins
}

fun View.setViewPadding(padding: Int) {
    this.setPadding(padding, padding, padding, padding)
}

fun View.setLayoutGravity(gravity: Int) {
    if (layoutParams is LinearLayout.LayoutParams) {
        val params = layoutParams as LinearLayout.LayoutParams
        params.gravity = gravity
        layoutParams = params
    } else if (layoutParams is FrameLayout.LayoutParams) {
        val params = layoutParams as FrameLayout.LayoutParams
        params.gravity = gravity
        layoutParams = params
    }
}
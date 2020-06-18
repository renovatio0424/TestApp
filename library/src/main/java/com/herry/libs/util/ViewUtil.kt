package com.herry.libs.util

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.herry.libs.helper.ApiHelper

@Suppress("MemberVisibilityCanBePrivate", "unused")
object ViewUtil {
    fun makeFullScreenWithTransparentStatusBar(activity: Activity?) {
        activity ?: return
        activity.window ?: return

        val window = activity.window
        if (ApiHelper.hasLollipop()) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val decorView = window.decorView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    fun inflate(@LayoutRes layout: Int, root: ViewGroup): View {
        return LayoutInflater.from(root.context).inflate(layout, root, false)
    }

    fun inflate(@LayoutRes layout: Int, context: Context): View {
        return LayoutInflater.from(context).inflate(layout, null, false)
    }

    fun removeAllViews(view: View?) {
        if (view !is ViewGroup) {
            return
        }
        view.removeAllViews()
    }

    fun removeView(parent: View?, position: Int): Boolean {
        if (parent !is ViewGroup) {
            return false
        }
        val view: View = getChildAt(parent, position) ?: return false
        parent.removeView(view)
        return true
    }

    fun getChildPosition(parent: View?, view: View): Int {
        if (parent !is ViewGroup) {
            return -1
        }
        for (index in 0 until parent.childCount) {
            val child = parent.getChildAt(index)
            if (child === view) {
                return index
            }
        }
        return -1
    }

    fun addView(parent: View?, vararg child: View?) {
        if (parent !is ViewGroup) {
            return
        }
        for (view in child) {
            if (null == view) {
                continue
            }
            parent.addView(view)
        }
    }

    fun getChildAt(parent: View?, index: Int): View? {
        if (parent !is ViewGroup || 0 > index) {
            return null
        }
        return parent.getChildAt(index)
    }

    fun getChildCount(parent: View?): Int {
        if (parent !is ViewGroup) {
            return 0
        }
        return parent.childCount
    }

    fun getColor(context: Context?, @ColorRes id: Int): Int {
        return if (null == context || 0 == id) {
            0
        } else ContextCompat.getColor(context, id)
    }

    fun getColorStateList(context: Context?, @ColorRes id: Int): ColorStateList? {
        return if (null == context || 0 == id) {
            null
        } else ContextCompat.getColorStateList(context, id)
    }

    fun getDrawable(context: Context?, @DrawableRes id: Int): Drawable? {
        if (null == context) {
            return null
        }
        return if (0 != id) {
            ContextCompat.getDrawable(context, id)
        } else {
            null
        }
    }

    fun getColorDrawable(context: Context?, @ColorRes id: Int): Drawable? {
        if (null == context || 0 == id) {
            return null
        }
        val color = ContextCompat.getColor(context, id)
        return ColorDrawable(color)
    }
}
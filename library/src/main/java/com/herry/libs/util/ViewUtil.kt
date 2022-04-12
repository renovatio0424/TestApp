package com.herry.libs.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.ResultReceiver
import android.view.*
import android.view.inputmethod.InputMethodManager
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

    fun hideSoftKeyboard(context: Context?, rootView: View?) {
        if (isSoftKeyboardShown(rootView) && context != null) {
            val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
            inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
        }
    }

    fun hideSoftKeyboard(view: View?, activity: Activity?): Boolean {
        return hideSoftKeyboard(view, activity, 0, null)
    }

    fun hideSoftKeyboard(view: View?, activity: Activity?, flag: Int): Boolean {
        return hideSoftKeyboard(view, activity, flag, null)
    }

    fun hideSoftKeyboard(view: View?, activity: Activity?, resultReceiver: ResultReceiver?): Boolean {
        return hideSoftKeyboard(view, activity, 0, resultReceiver)
    }

    fun hideSoftKeyboard(view: View?, activity: Activity?, flag: Int, resultReceiver: ResultReceiver?): Boolean {
        if (null == activity || null == view) {
            return false
        }
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        return null != imm && imm.hideSoftInputFromWindow(view.applicationWindowToken, flag, resultReceiver)
    }

    fun showSoftKeyboard(view: View?, activity: Activity?, flag: Int): Boolean {
        return showSoftKeyboard(view, activity, flag, null)
    }

    fun showSoftKeyboard(view: View?, activity: Activity?, resultReceiver: ResultReceiver?): Boolean {
        return showSoftKeyboard(view, activity, 0, resultReceiver)
    }

    fun showSoftKeyboard(view: View?, activity: Activity?, flag: Int, resultReceiver: ResultReceiver?): Boolean {
        if (null == activity || null == view) {
            return false
        }
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        return null != imm && imm.showSoftInput(view, flag, resultReceiver)
    }

    fun isSoftKeyboardShown(rootView: View?): Boolean {
        if (null == rootView) {
            return false
        }

        /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        @Suppress("LocalVariableName")
        val SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val dm = rootView.resources.displayMetrics

        /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        val heightDiff = rootView.bottom - r.bottom

        /* Threshold size: dp to pixels, multiply with display density */return heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density
    }

    @JvmStatic
    @SuppressLint("ClickableViewAccessibility")
    fun setProtectTouchLowLayer(view: View?, protect: Boolean) {
        view?.setOnTouchListener { _: View?, _: MotionEvent? -> protect }
    }

    fun removeViewFormParent(view: View?) {
        view ?: return

        if (view.parent is ViewGroup) {
            val parent = view.parent as ViewGroup
            parent.removeView(view)
        }
    }
}
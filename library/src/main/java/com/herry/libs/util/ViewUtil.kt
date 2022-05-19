package com.herry.libs.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.ResultReceiver
import android.util.DisplayMetrics
import android.util.Size
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
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

    fun getStatusBarHeight(context: Context?): Int {
        var result = 0
        if (null != context) {
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
        }
        return result
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
        if (null == context || 0 == id) {
            return 0
        }

        return try {
            ContextCompat.getColor(context, id)
        } catch (ex: Exception) {
            0
        }
    }

    fun getColorStateList(context: Context?, @ColorRes id: Int): ColorStateList? {
        if (null == context || 0 == id) {
            return null
        }

        return try {
            ContextCompat.getColorStateList(context, id)
        } catch (ex: Exception) {
            null
        }
    }

    fun getDrawable(context: Context?, @DrawableRes id: Int): Drawable? {
        if (null == context || 0 == id) {
            return null
        }

        return try {
            ContextCompat.getDrawable(context, id)
        } catch (ex: Exception) {
            null
        }
    }

    fun getColorDrawable(context: Context?, @ColorRes id: Int): Drawable? {
        if (null == context || 0 == id) {
            return null
        }
        val color = getColor(context, id)
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

    fun getDimension(context: Context?, id: Int): Float {
        val resources = context?.resources ?: return 0f
        return resources.getDimension(id)
    }

    fun getDimensionPixelSize(context: Context?, @DimenRes id: Int): Int {
        val resources = context?.resources ?: return 0
        return resources.getDimensionPixelSize(id)
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDpToPixel(dp: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        //float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        //Trace.d("Herry", "convertDpToPixel dp:" + dp + " to px:" + px);
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    fun convertPixelsToDp(px: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        //float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        //Trace.d("Herry", "convertPixelsToDp px:" + px + " to dp:" + dp);
        return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun getScreenSize(context: Context?): Size {
        val resources = context?.resources ?: return Size(0, 0)

        val displayMetrics = resources.displayMetrics ?: return Size(0, 0)
        return Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    fun isTabletDevice(context: Context?): Boolean = (context?.resources?.configuration?.smallestScreenWidthDp ?: 0) >= 600

    fun isPortraitOrientation(context: Context?): Boolean = context?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT
}
package com.herry.libs.helper

import android.app.Activity
import android.text.TextUtils
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes

@Suppress("MemberVisibilityCanBePrivate", "unused")
object ToastHelper {
    enum class Length(val value: Int) {
        SHORT(Toast.LENGTH_SHORT),
        LONG(Toast.LENGTH_LONG)
    }

    private var toast: Toast? = null

    fun showToast(activity: Activity?, @StringRes stringRes: Int, length: Length = Length.SHORT) {
        activity?.let { _activity ->
            _activity.runOnUiThread {
                cancel()
                if(stringRes != 0) {
                    toast = Toast.makeText(_activity, stringRes, length.value)
                    setToastLocation(_activity)

                    toast?.view?.findViewById<TextView>(android.R.id.message)?.apply {
                        gravity = Gravity.CENTER
                    }
                    toast?.show()
                }
            }
        }
    }

    fun showToast(activity: Activity?, message: String?, length: Length = Length.SHORT) {
        activity?.let { _activity ->
            _activity.runOnUiThread {
                cancel()
                if(!TextUtils.isEmpty(message)) {
                    toast = Toast.makeText(_activity, message, length.value)
                    setToastLocation(_activity)

                    toast?.view?.findViewById<TextView>(android.R.id.message)?.apply {
                        gravity = Gravity.CENTER
                    }
                    toast?.show()
                }
            }
        }
    }

    fun cancel() {
        toast?.cancel()
        toast = null
    }

    private fun setToastLocation(activity: Activity?) {
        activity?.resources?.displayMetrics?.let {
            val height = it.heightPixels

            val xOffset = 0
            val yOffset = (height * (3f / 10f)).toInt()
            val gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            toast?.setGravity(gravity, xOffset, yOffset)
        }
    }
}
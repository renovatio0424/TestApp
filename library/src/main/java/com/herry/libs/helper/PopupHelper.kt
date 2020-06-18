package com.herry.libs.helper

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import androidx.annotation.StringRes

@Suppress("MemberVisibilityCanBePrivate", "unused")
class PopupHelper(private val activity : (() -> Activity?)? = null) {

    private var dialog: AlertDialog? = null

    fun dismiss() {
        dialog?.let {
            if(it.isShowing) {
                it.dismiss()
            }
        }
        dialog = null
    }

    fun showPopup(dialog: AlertDialog) {
        dismiss()
        this.dialog = dialog
        dialog.show()
    }

    fun showPopup(
        @StringRes title: Int = 0,
        @StringRes message: Int = 0,
        listener: DialogInterface.OnClickListener? = null,
        cancelable: Boolean = true
    ) {
        val activity = activity?.run { this() }

        activity?.let { _activity ->
            showPopup(
                AlertDialog.Builder(_activity).apply {
                    if (title != 0) {
                        setTitle(title)
                    }
                    if (message != 0) {
                        setMessage(message)
                    }
                    setPositiveButton(android.R.string.ok, listener)
                    setCancelable(cancelable)
                }.create()
            )
        }
    }

    fun showPopup(
        title: String = "",
        message: String = "",
        listener: DialogInterface.OnClickListener? = null,
        cancelable: Boolean = true
    ) {
        val activity = activity?.run { this() }

        activity?.let { _activity ->
            showPopup(
                AlertDialog.Builder(_activity).apply {
                    setTitle(title)
                    setMessage(message)
                    setPositiveButton(android.R.string.ok, listener)
                    setCancelable(cancelable)
                }.create()
            )
        }
    }

    fun showPopup(
        title: String = "",
        message: String = "",
        positiveListener: DialogInterface.OnClickListener? = null,
        positiveText: String = "",
        negativeListener: DialogInterface.OnClickListener? = null,
        negativeText: String = ""
    ) {
        val activity = activity?.run { this() }

        activity?.let { _activity ->
            showPopup(
                AlertDialog.Builder(_activity).apply {
                    setTitle(title)
                    setMessage(message)
                    positiveListener?.let {
                            onClickListener ->
                        if(positiveText.isNotBlank()) {
                            setPositiveButton(positiveText, onClickListener)
                        } else {
                            setPositiveButton(android.R.string.ok, onClickListener)
                        }
                    }
                    negativeListener?.let { onClickListener ->
                        if(negativeText.isNotBlank()) {
                            setNegativeButton(negativeText, onClickListener)
                        } else {
                            setNegativeButton(android.R.string.cancel, onClickListener)
                        }
                    }
                }.create()
            )
        }
    }
}
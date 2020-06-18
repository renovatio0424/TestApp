package com.herry.test.app.base.activity_caller

import android.app.Activity
import android.content.Intent

interface ACModule {
    interface OnListener<T : ACModule> {
        fun getActivity(): Activity
        fun onDone(module: T)
    }

    interface OnPermissionListener {
        fun checkPermission(
            permission: Array<String>,
            blockRequest: Boolean,
            showBlockPopup: Boolean,
            onGranted: ((permission: Array<String>) -> Unit)?,
            onDenied: ((permission: Array<String>) -> Unit)?,
            onBlocked: ((permission: Array<String>) -> Unit)?
        )
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean
    fun call()
}
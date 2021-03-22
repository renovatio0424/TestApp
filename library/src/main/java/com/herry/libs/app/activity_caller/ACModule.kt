package com.herry.libs.app.activity_caller

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityOptionsCompat
import com.herry.libs.app.activity_caller.module.ACNavigation

interface ACModule {
    interface OnListener<T : ACModule> {
        fun getActivity(): ComponentActivity

        fun launchActivity(
            intent: Intent,
            options: ActivityOptionsCompat?,
            onResult: ((result: ACNavigation.Result) -> Unit)? = null
        ) {}
    }

    interface OnPermissionListener {
        fun requestPermission(
            permission: Array<String>,

            onGranted: ((permission: Array<String>) -> Unit)?,
            onDenied: ((permission: Array<String>) -> Unit)?,
            onBlocked: ((permission: Array<String>) -> Unit)?
        )
    }

    fun call()
}
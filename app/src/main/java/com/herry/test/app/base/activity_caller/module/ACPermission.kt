package com.herry.test.app.base.activity_caller.module

import android.content.Intent
import com.herry.test.app.base.activity_caller.ACModule

class ACPermission(private val caller: Caller, private val listener: ACPermissionListener): ACModule {

    class Caller(
        internal val permissions: Array<String>,
        internal val blockRequest: Boolean = false,
        internal val onGranted: ((permission: Array<String>) -> Unit)? = null,
        internal val onDenied: ((permission: Array<String>) -> Unit)?  = null,
        internal val onBlocked: ((permission: Array<String>) -> Unit)?  = null
    )

    interface ACPermissionListener: ACModule.OnListener<ACPermission>, ACModule.OnPermissionListener

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return false
    }

    override fun call() {
        listener.checkPermission(
            caller.permissions,
            caller.blockRequest,
            caller.onBlocked == null,
            caller.onGranted,
            caller.onDenied,
            caller.onBlocked
        )
    }
}
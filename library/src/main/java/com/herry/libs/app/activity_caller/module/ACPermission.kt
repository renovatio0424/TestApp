package com.herry.libs.app.activity_caller.module

import com.herry.libs.app.activity_caller.ACModule

class ACPermission(private val caller: Caller, private val listener: ACPermissionListener): ACModule {

    class Caller(
        internal val permissions: Array<String>,
        internal val onGranted: ((permission: Array<String>) -> Unit)? = null,
        internal val onDenied: ((permission: Array<String>) -> Unit)?  = null,
        internal val onBlocked: ((permission: Array<String>) -> Unit)?  = null
    )

    interface ACPermissionListener: ACModule.OnListener<ACPermission>, ACModule.OnPermissionListener

    override fun call() {
        listener.requestPermission(
            caller.permissions,
            caller.onGranted,
            caller.onDenied,
            caller.onBlocked
        )
    }
}
package com.herry.libs.app.activity_caller.activity

import com.herry.libs.app.activity_caller.module.ACNavigation

internal class LaunchActivityResults {
    var onResult: ((result: ACNavigation.Result) -> Unit)? = null
}

@Suppress("unused")
internal class RequestPermissionResults {
    var onResult: ((granted: Array<String>, denied: Array<String>, blocked: Array<String>) -> Unit)? = null
    val grantedPermissions = mutableListOf<String>()
    val deniedPermissions = mutableListOf<String>()
    val blockedPermissions = mutableListOf<String>()
}

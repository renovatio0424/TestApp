package com.herry.libs.app.activity_caller.activity

import android.net.Uri
import com.herry.libs.app.activity_caller.module.ACNavigation
import com.herry.libs.app.activity_caller.module.ACTake

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

internal class TakePictureResults {
    var uri: Uri? = null
    var onResult: ((result: ACTake.Result) -> Unit)? = null
}

internal class TakeVideoResults {
    var uri: Uri? = null
    var onResult: ((result: ACTake.Result) -> Unit)? = null
}
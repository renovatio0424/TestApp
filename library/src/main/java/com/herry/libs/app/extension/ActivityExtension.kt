package com.herry.libs.app.extension

import android.app.Activity
import com.herry.libs.app.activity_caller.module.ACNavigation
import com.herry.libs.app.activity_caller.module.ACPermission
import com.herry.libs.app.base.ActivityEx

fun Activity.callStartActivity(caller: ACNavigation.IntentCaller) {
    if (this is ActivityEx) {
        this.call(caller)
    }
}

fun Activity.callStartActivity(caller: ACPermission.Caller) {
    if (this is ActivityEx) {
        this.call(caller)
    }
}
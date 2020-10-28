package com.herry.test.app.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.herry.libs.app.activity_caller.AC
import com.herry.libs.app.activity_caller.ACBase
import com.herry.libs.app.base.ActivityEx
import com.herry.libs.helper.PopupHelper
import com.herry.libs.helper.ApiHelper
import com.herry.libs.util.AppActivityManager

@Suppress("PrivatePropertyName")
abstract class BaseActivity : ActivityEx() {

    @SuppressLint("SourceLockedOrientationActivity")
    open fun onActivityOrientation() {
        if (ApiHelper.hasOreo()) {
            try {
//                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
            } catch (ex: IllegalStateException) {
                // isTranslucentOrFloating
//                Trace.e("Oreo", "Only fullscreen opaque activities can request orientation")
            }

        } else {
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
    }

    override fun onResume() {
        super.onResume()

        // checks changed application by user
        if ((application is BaseApplication) && (application as BaseApplication).isNeedRestartApp()) {
            (application as BaseApplication).resetRestartApp()
            // restart application
            packageManager.getLaunchIntentForPackage(packageName)?.run {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(this)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        hideBlockedPermissionPopup()

//        if(notificationBroadcastReceiverRegister) {
//            LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(notificationBroadcastReceiver)
//            notificationBroadcastReceiverRegister = false
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (aC.activityResult(requestCode, resultCode, data)) {
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private var blockedPermissionPopupHelper: PopupHelper? = null
    private fun hideBlockedPermissionPopup() {
        blockedPermissionPopupHelper?.dismiss()
        blockedPermissionPopupHelper = null
    }

    private fun getActivity() : BaseActivity = this

    override fun showBlockedPermissionPopup() {
        hideBlockedPermissionPopup()

        blockedPermissionPopupHelper = PopupHelper(::getActivity)
        blockedPermissionPopupHelper?.showPopup(
            title = "Setting permissions",
            message = "Permission settings are turned off and can not access those services.\n\nPlease turn in [Settings] > [authority].",
            positiveListener = DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                applicationContext?.let {
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.parse("package:" + applicationContext.packageName))
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                        val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                        startActivity(intent)
                    }
                }
            },
            negativeListener = DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            }
        )
    }

    protected fun finish(withoutAnimation: Boolean) {
        super.finish()
        if (withoutAnimation) overridePendingTransition(0, 0)
    }

    open fun getActivityManager(): AppActivityManager? {
        return if (application is BaseApplication) {
            (application as BaseApplication).appActivityManager
        } else {
            null
        }
    }
}
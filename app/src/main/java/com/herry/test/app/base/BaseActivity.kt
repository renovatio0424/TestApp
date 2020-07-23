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
import com.herry.test.app.base.activity_caller.AC
import com.herry.test.app.base.activity_caller.ACBase
import com.herry.libs.helper.PopupHelper
import com.herry.libs.helper.ApiHelper
import com.herry.libs.util.AppActivityManager

@Suppress("PrivatePropertyName")
abstract class BaseActivity : AppCompatActivity(), AC {

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

    private val aC = ACBase(object : ACBase.ACBaseListener {
        override fun getActivity(): Activity = this@BaseActivity

        override fun checkPermission(
            permission: Array<String>,
            blockRequest: Boolean,
            showBlockPopup: Boolean,
            onGranted: ((permission: Array<String>) -> Unit)?,
            onDenied: ((permission: Array<String>) -> Unit)?,
            onBlocked: ((permission: Array<String>) -> Unit)?
        ) {
            checkPermission(permission, blockRequest, { _permission ->
                onGranted?.let { it(_permission) }
            }, { _permission ->
                onDenied?.let { it(_permission) }
            }, { _permission ->
                if (showBlockPopup) showBlockedPermissionPopup()
                onBlocked?.let { it(_permission) }
            })
        }
    })

    override fun <T> call(caller: T) {
        aC.call(caller)
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

    private class PermissionResults(
        val onResults: (granted: Array<String>, denied: Array<String>, blocked: Array<String>) -> Unit
    ) {
        val grantedPermissions = mutableListOf<String>()
        val blockedPermissions = mutableListOf<String>()
    }

    private var permissionResults: PermissionResults? = null

    private val PERMISSIONS_REQUEST_RESULT = 1000

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_RESULT) {
            permissionResults?.let {
                val deniedPermissions = mutableListOf<String>()
                for (i in grantResults.indices) {
                    if (i < permissions.size) {
                        when (grantResults[i]) {
                            PackageManager.PERMISSION_GRANTED -> it.grantedPermissions.add(
                                permissions[i]
                            )
                            PackageManager.PERMISSION_DENIED -> deniedPermissions.add(permissions[i])
                        }
                    }
                }

                it.onResults(
                    it.grantedPermissions.toTypedArray(),
                    deniedPermissions.toTypedArray(),
                    it.blockedPermissions.toTypedArray()
                )
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun checkPermission(
        permissions: Array<String>,
        blockRequest: Boolean,
        onGranted: ((permission: Array<String>) -> Unit)? = null,
        onDenied: ((permission: Array<String>) -> Unit)? = null,
        onBlocked: ((permission: Array<String>) -> Unit)? = null
    ) {
        if (permissions.isEmpty()) {
            onGranted?.let { it(permissions) }
            return
        }

        if (ApiHelper.hasMarshmallow()) {
            val requestPermissions = mutableListOf<String>()

            val permissionResults =
                PermissionResults { granted: Array<String>, denied: Array<String>, blocked: Array<String> ->
                    when {
                        denied.isNotEmpty() -> onDenied?.let { it(denied) }
                        blocked.isNotEmpty() -> onBlocked?.let { it(blocked) }
                        else -> onGranted?.let { it(granted) }
                    }
                }

            for (permission in permissions) {
                if (TextUtils.isEmpty(permission)) {
                    continue
                }

                if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                    permissionResults.grantedPermissions.add(permission)
                } else if (shouldShowRequestPermissionRationale(permission) && !blockRequest) {
                    permissionResults.blockedPermissions.add(permission)
                } else {
                    requestPermissions.add(permission)
                }
            }

            if (requestPermissions.isNotEmpty()) {
                this@BaseActivity.permissionResults = permissionResults
                requestPermissions(
                    requestPermissions.toTypedArray(),
                    PERMISSIONS_REQUEST_RESULT
                )
            } else {
                permissionResults.onResults(
                    permissionResults.grantedPermissions.toTypedArray(),
                    arrayOf(),
                    permissionResults.blockedPermissions.toTypedArray()
                )
            }
        } else {
            onGranted?.let { it(permissions) }
        }
    }

    private var blockedPermissionPopupHelper: PopupHelper? = null
    private fun hideBlockedPermissionPopup() {
        blockedPermissionPopupHelper?.dismiss()
        blockedPermissionPopupHelper = null
    }

    private fun getActivity() : BaseActivity = this

    fun showBlockedPermissionPopup() {
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
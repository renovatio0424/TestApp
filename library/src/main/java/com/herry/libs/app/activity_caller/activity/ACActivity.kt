package com.herry.libs.app.activity_caller.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.herry.libs.app.activity_caller.AC
import com.herry.libs.app.activity_caller.ACBase
import com.herry.libs.helper.ApiHelper

abstract class ACActivity : AppCompatActivity(), AC {

    protected val activityCaller = ACBase(object : ACBase.ACBaseListener {
        override fun getActivity(): Activity = this@ACActivity
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
        activityCaller.call(caller)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (activityCaller.activityResult(requestCode, resultCode, data)) {
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

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

    abstract fun showBlockedPermissionPopup()

    private class PermissionResults(
        val onResults: (granted: Array<String>, denied: Array<String>, blocked: Array<String>) -> Unit
    ) {
        val grantedPermissions = mutableListOf<String>()
        val blockedPermissions = mutableListOf<String>()
    }

    private var permissionResults: PermissionResults? = null

    companion object {
        private const val PERMISSIONS_REQUEST_RESULT = 1000
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
                this@ACActivity.permissionResults = permissionResults
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

}
package com.herry.libs.app.activity_caller.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.herry.libs.app.activity_caller.module.ACNavigation
import com.herry.libs.app.nav.NavMovement

class ActivityResultLaunchers(private val activity: ComponentActivity) {

    internal class ActivityResultViewModel : ViewModel() {
        val launchActivityResult = LaunchActivityResults()
        val requestPermissionResults = RequestPermissionResults()
    }

    private val activityResultViewModel: ActivityResultViewModel = ViewModelProvider(activity).get(ActivityResultViewModel::class.java)

    private val activityLauncher: ActivityResultLauncher<Intent> = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        onLaunchActivityResult(activityResult)
    }

    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grantResults: Map<String, Boolean> ->
        onRequestPermissionResult(grantResults)
    }

    fun processRequestPermission(
        permissions: Array<String>,
        onGranted: ((permission: Array<String>) -> Unit)? = null,
        onDenied: ((permission: Array<String>) -> Unit)? = null,
        onBlocked: ((permission: Array<String>) -> Unit)? = null
    ) {
        if (permissions.isEmpty()) {
            onGranted?.let { it(permissions) }
            return
        }

        activityResultViewModel.requestPermissionResults.onResult = { granted: Array<String>, denied: Array<String>, blocked: Array<String> ->
            when {
                denied.isNotEmpty() -> onDenied?.let { it(denied) }
                blocked.isNotEmpty() -> onBlocked?.let { it(blocked) }
                else -> onGranted?.let { it(granted) }
            }
        }

        val grantedPermissions = mutableListOf<String>()
        val requestPermissions = mutableListOf<String>()
        for (permission in permissions) {
            if (TextUtils.isEmpty(permission)) {
                continue
            }

            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(permission)
            } else {
                requestPermissions.add(permission)
            }
        }

        if (requestPermissions.isNotEmpty()) {
            activityResultViewModel.requestPermissionResults.apply {
                this.grantedPermissions.clear()
                this.grantedPermissions.addAll(grantedPermissions)
            }
            requestPermissionLauncher.launch(requestPermissions.toTypedArray())
        } else {
            activityResultViewModel.requestPermissionResults.onResult?.invoke(
                grantedPermissions.toTypedArray(),
                arrayOf(),
                arrayOf()
            )

            activityResultViewModel.requestPermissionResults.onResult = null
        }
    }

    fun processLaunchActivity(intent: Intent, options: ActivityOptionsCompat?, onResult: ((result: ACNavigation.Result) -> Unit)?) {
        activityResultViewModel.launchActivityResult.onResult = onResult

        activityLauncher.launch(intent, options)
    }

    private fun onLaunchActivityResult(activityResult: ActivityResult) {
        activityResultViewModel.launchActivityResult.onResult?.invoke(
            ACNavigation.Result(
                activity,
                activityResult.resultCode,
                activityResult.data,
                activityResult.data?.getBundleExtra(NavMovement.NAV_BUNDLE)
            )
        )

        activityResultViewModel.launchActivityResult.onResult = null
    }

    private fun onRequestPermissionResult(grantResults: Map<String, Boolean>) {
        val permissionResults = activityResultViewModel.requestPermissionResults

        if (grantResults.isEmpty()) {
            return
        }

        val grantedPermissions = mutableListOf<String>().apply {
            addAll(permissionResults.grantedPermissions)
        }
        val deniedPermissions = mutableListOf<String>()
        val blockedPermissions = mutableListOf<String>()

        grantResults.keys.forEach { permission ->
            val isGranted = grantResults[permission] == true
            if (isGranted) {
                grantedPermissions.add(permission)
            } else {
                if (activity.shouldShowRequestPermissionRationale(permission)) {
                    // denied
                    deniedPermissions.add(permission)
                } else {
                    // blocked
                    blockedPermissions.add(permission)
                }
            }
        }

        permissionResults.onResult?.invoke(
            grantedPermissions.toTypedArray(),
            deniedPermissions.toTypedArray(),
            blockedPermissions.toTypedArray()
        )

        activityResultViewModel.requestPermissionResults.onResult = null
    }

    fun unregisterAll() {
        requestPermissionLauncher.unregister()
        activityLauncher.unregister()
    }
}
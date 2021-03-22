package com.herry.libs.app.activity_caller.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.herry.libs.app.activity_caller.module.ACNavigation
import com.herry.libs.app.activity_caller.module.ACTake
import com.herry.libs.app.nav.NavMovement

class ActivityResultLaunchers(private val activity: ComponentActivity) {

    internal class ActivityResultViewModel : ViewModel() {
        val launchActivityResult = LaunchActivityResults()
        val requestPermissionResults = RequestPermissionResults()
        val takePictureResult = TakePictureResults()
        val takeVideoResult = TakePictureResults()
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

    private val takePictureLauncher: ActivityResultLauncher<Uri> = activity.registerForActivityResult(
        object: ActivityResultContracts.TakePicture() {
            override fun createIntent(context: Context, input: Uri): Intent {
                val intent = super.createIntent(context, input)
                // adds permission to other app
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                return intent
            }
        }
    ) { success ->
        onTakePictureResult(success)
    }

    private val takeVideoLauncher: ActivityResultLauncher<Uri> = activity.registerForActivityResult(
        object: ActivityResultContracts.TakeVideo() {
            override fun createIntent(context: Context, input: Uri): Intent {
                val intent = super.createIntent(context, input)
                // adds permission to other app
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                return intent
            }
        }
    ) { thumbnail ->
        onTakeVideoResult(thumbnail)
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

    fun processTakePicture(uri: Uri, onResult: ((result: ACTake.Result) -> Unit)?) {
        activityResultViewModel.takePictureResult.uri = uri
        activityResultViewModel.takePictureResult.onResult = onResult

        takePictureLauncher.launch(uri)
    }

    private fun onTakePictureResult(success: Boolean) {
        activityResultViewModel.takePictureResult.onResult?.invoke(
            ACTake.Result(
                callActivity = activity,
                uri = activityResultViewModel.takePictureResult.uri,
                success = success
            )
        )

        activityResultViewModel.takePictureResult.uri = null
        activityResultViewModel.takePictureResult.onResult = null
    }

    fun processTakeVideo(uri: Uri, onResult: ((result: ACTake.Result) -> Unit)?) {
        activityResultViewModel.takeVideoResult.uri = uri
        activityResultViewModel.takeVideoResult.onResult = onResult

        takeVideoLauncher.launch(uri)
    }

    private fun onTakeVideoResult(thumbnail: Bitmap?) {
        activityResultViewModel.takeVideoResult.onResult?.invoke(
            ACTake.Result(
                callActivity = activity,
                uri = activityResultViewModel.takeVideoResult.uri,
                success = thumbnail != null
            )
        )

        activityResultViewModel.takeVideoResult.uri = null
        activityResultViewModel.takeVideoResult.onResult = null
    }

    fun unregisterAll() {
        requestPermissionLauncher.unregister()
        activityLauncher.unregister()
        takePictureLauncher.unregister()
        takeVideoLauncher.unregister()
    }
}
package com.herry.test.app.base

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.herry.libs.app.activity_caller.activity.ACActivity
import com.herry.libs.helper.ApiHelper
import com.herry.libs.helper.PopupHelper
import com.herry.libs.util.AppActivityManager
import com.herry.libs.util.AppUtil
import com.herry.libs.util.FragmentAddingOption

@Suppress("PrivatePropertyName")
abstract class BaseActivity : ACActivity() {

    @IdRes
    open fun getHostViewID(): Int? = null

    @LayoutRes
    open fun getContentViewID(): Int = -1

    open fun getStartFragment(): Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (getContentViewID() != -1) {
            createContentView()
        }
    }

    private fun createContentView() {
        setContentView(getContentViewID())

        getStartFragment()?.run {
            AppUtil.setFragment(this@BaseActivity, getHostViewID(),
                this,
                FragmentAddingOption(isReplace = true, isAddToBackStack = true)
            )
        }
    }


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
        if (activityCaller.activityResult(requestCode, resultCode, data)) {
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

    override fun onBackPressed() {
        val backStackFragment = AppUtil.getLastBackStackFragment(supportFragmentManager)
        if (null != backStackFragment && backStackFragment.fragment is BaseFragment) {
            val fragment = backStackFragment.fragment as BaseFragment

            if (fragment.onBackPressed()) {
                return
            }
        }

        super.onBackPressed()
    }
}
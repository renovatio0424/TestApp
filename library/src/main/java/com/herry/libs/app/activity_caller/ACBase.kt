package com.herry.libs.app.activity_caller

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.herry.libs.app.activity_caller.module.ACError
import com.herry.libs.app.activity_caller.module.ACInject
import com.herry.libs.app.activity_caller.module.ACNavigation
import com.herry.libs.app.activity_caller.module.ACPermission
import com.herry.libs.helper.PopupHelper

class ACBase(private val listener: ACBaseListener): AC {

    interface ACBaseListener: ACModule.OnPermissionListener {
        fun getActivity(): Activity
    }

    private var aCInject = ACInject(object : ACModule.OnListener<ACInject> {
        override fun getActivity(): Activity = listener.getActivity()

        override fun onDone(module: ACInject) {
        }
    })
    private val activityResultableACModules = mutableListOf<ACModule>()

    override fun <T> call(caller: T) {
        if(Looper.myLooper() == Looper.getMainLooper()) {
            callOnMainLooper(caller)
        } else {
            Handler(Looper.getMainLooper()).post {
                callOnMainLooper(caller)
            }
        }
    }

    private fun <T> callOnMainLooper(caller: T) {
        when(caller) {
            is ACPermission.Caller -> {
                val module = ACPermission(caller, object : ACPermission.ACPermissionListener {
                    override fun getActivity(): Activity = listener.getActivity()

                    override fun onDone(module: ACPermission) {
                    }

                    override fun checkPermission(
                        permission: Array<String>,
                        blockRequest: Boolean,
                        showBlockPopup: Boolean,
                        onGranted: ((permission: Array<String>) -> Unit)?,
                        onDenied: ((permission: Array<String>) -> Unit)?,
                        onBlocked: ((permission: Array<String>) -> Unit)?
                    ) = listener.checkPermission(permission, blockRequest, showBlockPopup, onGranted, onDenied, onBlocked)
                })
                module.call()
            }
            is ACInject.Caller<*> -> {
                aCInject.call(caller)
            }
            is ACError.Caller -> {
                val module = ACError(caller, object : ACError.ACErrorListener {
                    override fun getActivity(): Activity = listener.getActivity()

                    override fun onDone(module: ACError) {}

                    override fun getPopupHelper(): PopupHelper = aCInject.call(PopupHelper::class)
                })
                module.call()
            }
            is ACNavigation.Caller -> {
                val module = ACNavigation(caller, object : ACModule.OnListener<ACNavigation> {

                    override fun getActivity(): Activity = listener.getActivity()

                    override fun onDone(module: ACNavigation) {
                        done(module)
                    }
                })
                activityResultableACModules.add(module)
                module.call()
            }
        }
    }

    fun activityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        for(module in activityResultableACModules) {
            if(module.onActivityResult(requestCode, resultCode, data)) {
                return true
            }
        }
        return false
    }

    private fun done(module: ACModule) {
        activityResultableACModules.remove(module)
    }
}
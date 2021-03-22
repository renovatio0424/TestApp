package com.herry.test.app.base.ac

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.herry.libs.app.activity_caller.ACModule
import com.herry.libs.app.activity_caller.module.ACNavigation
import com.herry.test.app.base.BaseFragment
import com.herry.test.app.base.SingleActivity
import kotlin.reflect.KClass

class AppACNavigation(private val caller: Caller, private val listener: ACModule.OnListener<ACNavigation>) : ACNavigation(caller, listener) {

    class SingleCaller (
        internal val cls: KClass<out BaseFragment>,
        internal val bundle: Bundle? = null,
//        internal val systemUiVisibility: Int = 0,
//        internal val statusBarColor: Int = 0,vgb
        internal val transparentStatusBarFullScreen: Boolean = false,
        transitions: Array<Transition>? = null,
        onResult: ((result: Result) -> Unit)? = null
    ) : Caller(transitions, onResult)

    override fun getCallerIntent(activity: Activity): Intent? {
        return when(caller) {
            is SingleCaller -> {
                Intent(activity, SingleActivity::class.java).apply {
                    putExtra(SingleActivity.FRAGMENT_CLASS_NAME, caller.cls.qualifiedName)
                    caller.bundle?.let {
                        putExtra(SingleActivity.FRAGMENT_BUNDLE, it)
                    }
//                        putExtra(SingleActivity.FRAGMENT_SYSTEM_UI_VISIBILITY, caller.systemUiVisibility)
//                        putExtra(SingleActivity.FRAGMENT_STATUS_BAR_COLOR, caller.statusBarColor)
                    putExtra(SingleActivity.FRAGMENT_TRANSPARENT_STATUS_BAR, caller.transparentStatusBarFullScreen)
                }
            }
            else -> super.getCallerIntent(activity)
        }
    }


}
package com.herry.test.app.base.ac

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.herry.libs.app.activity_caller.ACModule
import com.herry.libs.app.activity_caller.module.ACNavigation
import com.herry.libs.app.nav.NavDestination
import com.herry.test.app.base.BaseActivity
import com.herry.test.app.base.BaseFragment
import com.herry.test.app.base.SingleActivity
import kotlin.reflect.KClass

class AppACNavigation(private val caller: Caller, private val listener: ACModule.OnListener<ACNavigation>) : ACNavigation(caller, listener) {

    class NavCaller (
        internal val cls: Class<out BaseActivity>,
        internal val bundle: Bundle? = null,
        internal val startDestination: Int = 0,
        useTransition: Boolean = true,
        transitions: Array<Transition>? = null,
        result: ((resultCode: Int, intent: Intent?, bundle: Bundle?) -> Unit)? = null
    ) : Caller(useTransition, transitions, result)

    class SingleCaller (
        internal val cls: KClass<out BaseFragment>,
        internal val bundle: Bundle? = null,
//        internal val systemUiVisibility: Int = 0,
//        internal val statusBarColor: Int = 0,
        internal val transparentStatusBarFullScreen: Boolean = false,
        useTransition: Boolean = true,
        transitions: Array<Transition>? = null,
        result: ((resultCode: Int, intent: Intent?, bundle: Bundle?) -> Unit)? = null
    ) : Caller(useTransition, transitions, result)

    override fun getCallerIntent(activity: Activity): Intent? {
        return when(caller) {
            is NavCaller -> {
                Intent(activity, caller.cls).apply {
                    caller.bundle?.let {
                        putExtra(NavDestination.NAV_BUNDLE, it)
                    }
                    if (caller.startDestination != 0) {
                        putExtra(NavDestination.NAV_START_DESTINATION, caller.startDestination)
                    }
                }
            }
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
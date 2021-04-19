package com.herry.libs.app.nav

import android.os.Bundle

object NavBundleUtil {
    fun isNavigationResultOk(bundle: Bundle?): Boolean {
        return bundle != null && bundle.getBoolean(NavMovement.NAV_UP_RESULT_OK, false)
    }

    fun isNavigationUpBlocked(bundle: Bundle?): Boolean {
        return bundle != null && bundle.getBoolean(NavMovement.NAV_UP_BLOCK, false)
    }

    fun getNavigationAction(bundle: Bundle?): String {
        return bundle?.getString(NavMovement.NAV_ACTION_KEY, "") ?: ""
    }

    fun createNavigationAction(action: String): Bundle {
        if (action.isBlank()) {
            throw IllegalArgumentException("action is blank")
        }

        return Bundle().apply {
            putString(NavMovement.NAV_ACTION_KEY, action)
        }
    }

    fun fromNavigationId(bundle: Bundle?): Int {
        return bundle?.getInt(NavMovement.NAV_UP_FROM_ID, 0) ?: 0
    }

    fun createNavigationBundle(resultOk: Boolean): Bundle {
        val bundle = Bundle()
        bundle.putBoolean(NavMovement.NAV_UP_RESULT_OK, resultOk)
        return bundle
    }

    fun createBlockNavigateUp(): Bundle = Bundle().apply {
        putBoolean(NavMovement.NAV_UP_BLOCK, true)
    }
}
package com.herry.libs.app.nav

import android.os.Bundle

@Suppress("unused")
object NavBundleUtil {
    fun isNavigationResultOk(bundle: Bundle?): Boolean {
        return bundle != null && bundle.getBoolean(NavMovement.NAV_UP_RESULT_OK, false)
    }

    fun getNavigationAction(bundle: Bundle?): String {
        return bundle?.getString(NavMovement.NAV_ACTION_KEY, "") ?: ""
    }

    fun createNavigationAction(action: String, bundle: Bundle? = null): Bundle {
        if (action.isBlank()) {
            throw IllegalArgumentException("action is blank")
        }

        return (bundle ?: Bundle()).apply {
            putString(NavMovement.NAV_ACTION_KEY, action)
        }
    }

    fun fromNavigationId(bundle: Bundle?): Int {
        return bundle?.getInt(NavMovement.NAV_UP_FROM_ID, 0) ?: 0
    }

    fun addFromNavigationId(bundle: Bundle? = null, id: Int) {
        bundle ?: return
        bundle.putInt(NavMovement.NAV_UP_FROM_ID, id)
    }

    fun createNavigationBundleWidthFromId(id: Int): Bundle {
        return Bundle().apply {
            addFromNavigationId(this, id)
        }
    }

    fun addNavigationUpDestinationId(bundle: Bundle? = null, desId: Int): Bundle {
        return (bundle ?: Bundle()).apply {
            putInt(NavMovement.NAV_UP_DES_ID, desId)
        }
    }

    fun createNavigationBundle(resultOk: Boolean, result: Bundle? = null): Bundle {
        val bundle = result ?: Bundle()
        bundle.putBoolean(NavMovement.NAV_UP_RESULT_OK, resultOk)
        return bundle
    }
}
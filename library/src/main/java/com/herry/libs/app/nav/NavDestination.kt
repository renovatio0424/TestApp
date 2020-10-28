package com.herry.libs.app.nav

import android.os.Bundle

interface NavDestination {
    companion object {
        const val NAV_START_DESTINATION = "NAV_START_DESTINATION"
        const val NAV_BUNDLE = "NAV_BUNDLE"

        const val NAV_UP_BLOCK = "NAV_UP_BLOCK"
        const val NAV_UP_FROM_ID = "NAV_UP_FROM_ID"
        const val NAV_UP_DES_ID = "NAV_UP_DES_ID"
        const val NAV_UP_RESULT_OK = "NAV_UP_RESULT_OK"
    }

    fun onNavUp(): Bundle?

    fun onNavResults(bundle: Bundle)

    fun isTransition(): Boolean
}
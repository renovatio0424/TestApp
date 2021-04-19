package com.herry.libs.app.nav

import android.os.Bundle

interface NavMovement {
    companion object {
        const val NAV_START_DESTINATION = "NAV_START_DESTINATION"
        const val NAV_BUNDLE = "NAV_BUNDLE"

        const val NAV_UP_BLOCK = "NAV_UP_BLOCK"
        const val NAV_UP_FROM_ID = "NAV_UP_FROM_ID"
        const val NAV_UP_DES_ID = "NAV_UP_DES_ID"
        const val NAV_UP_RESULT_OK = "NAV_UP_RESULT_OK"

        const val NAV_FORM_RESULT = "from"
        const val NAV_ACTION_KEY = "action_key"
    }

    fun onNavigateUp(): Bundle?

    fun isTransition(): Boolean

    fun isSkipNavigateUp(): Boolean = false
}
package com.herry.test.app.bottomnav.helper

import androidx.fragment.app.Fragment
import com.herry.libs.app.nav.NavActionUtil
import com.herry.libs.widget.extension.notifyToNavHost

object NavScreenActionHelper {

    fun showSetting(fragment: Fragment) {
        fragment.notifyToNavHost(
            NavActionUtil.createActionDataBundle(NavScreenActions.SHOW_SETTINGS.key, null)
        )
    }
}
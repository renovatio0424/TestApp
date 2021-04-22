package com.herry.test.app.base.nestednav

import androidx.navigation.fragment.NavHostFragment
import com.herry.test.app.base.nav.BaseNavActivity

interface NestedNavMovement {
    fun addNestedNavHostFragment(subNavHostFragment: NavHostFragment?) {
        val activity = subNavHostFragment?.activity
        if (activity is BaseNavActivity) {
            activity.addChildNavHostFragment(subNavHostFragment)
        }
    }
}
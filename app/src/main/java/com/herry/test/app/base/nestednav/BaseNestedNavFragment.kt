package com.herry.test.app.base.nestednav

import androidx.navigation.fragment.NavHostFragment
import com.herry.test.app.base.nav.BaseNavFragment

@Suppress("SameParameterValue")
open class BaseNestedNavFragment: BaseNavFragment() {

    protected fun addNestedNavHostFragment(subNavHostFragment: NavHostFragment?) {
        val activity = requireActivity()
        if (activity is BaseNestedNavActivity) {
            activity.addNestedNavHostFragment(subNavHostFragment)
        }
    }
}
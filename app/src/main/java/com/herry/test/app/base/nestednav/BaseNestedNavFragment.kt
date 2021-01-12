package com.herry.test.app.base.nestednav

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.herry.test.app.base.nav.BaseNavFragment

@Suppress("SameParameterValue")
open class BaseNestedNavFragment: BaseNavFragment() {

    protected fun addSubNavHostFragment(subNavHostFragment: NavHostFragment?) {
        val activity = requireActivity()
        if (activity is BaseNestedNavActivity) {
            activity.addSubNavHostFragment(subNavHostFragment)
        }
    }
}
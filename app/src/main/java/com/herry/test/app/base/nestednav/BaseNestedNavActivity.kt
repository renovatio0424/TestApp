package com.herry.test.app.base.nestednav

import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.herry.libs.app.nav.NavMovement
import com.herry.libs.util.BundleUtil
import com.herry.libs.widget.extension.findParentNavHostFragment
import com.herry.libs.widget.extension.isCurrentStartDestinationFragment
import com.herry.libs.widget.extension.isParentViewVisible
import com.herry.test.app.base.nav.BaseNavActivity

abstract class BaseNestedNavActivity : BaseNavActivity() {

    fun addSubNavHostFragment(subNavHostFragment: NavHostFragment?) {
        addOnBackStackChangedListener(subNavHostFragment)
    }

    override fun getCurrentFragment(): Fragment? {
        val fragments = getActiveNavHostFragment()?.childFragmentManager?.fragments ?: mutableListOf()
        if (fragments.isNotEmpty()) {
            return fragments[0]
        }
        return null
    }

    override fun getCurrentDestination(): NavDestination? {
        val navHostFragment = getCurrentFragment()?.findParentNavHostFragment()
        return navHostFragment?.findNavController()?.currentDestination ?: navController?.currentDestination
    }

    private fun findNavHostFragment(fragment: Fragment?): NavHostFragment? {
        fragment ?: return null

        if (fragment is BaseNestedNavFragment) {
            val fragments = fragment.childFragmentManager.fragments
            for (childFragment in fragments) {
                return findNavHostFragment(childFragment)
            }
        }

        if (fragment is NavHostFragment) {
            return fragment
        }

        return null
    }

    override fun navigateUp(): Boolean {
        return getActiveNavHostFragment()?.navController?.navigateUp() ?: false
    }

    private fun getActiveNavHostFragment(): NavHostFragment? {
        val fragments = navHostFragment?.childFragmentManager?.fragments ?: mutableListOf()
        for (fragment in fragments) {
            val navHostFragment = findNavHostFragment(fragment)
            // checks sub NavHostFragment
            val subNavHostFragments = navHostFragment?.parentFragmentManager?.fragments ?: mutableListOf()
            val subNavHostFragment = subNavHostFragments.asReversed().firstOrNull { it.isParentViewVisible() }
            if (subNavHostFragment is NavHostFragment) {
                return subNavHostFragment
            }
        }

        return navHostFragment
    }

    override fun navigateUpAndResults(): Boolean {
        val fragment = getCurrentFragment()
        if (fragment is NavMovement) {
            if (fragment.isTransition()) {
                return false
            }

            results = fragment.onNavigateUp()?.apply {
                if (BundleUtil.isNavigationUpBlocked(this)) {
                    results = null
                    return false
                }
                getCurrentDestination()?.let {
                    putInt(NavMovement.NAV_UP_FROM_ID, it.id)
                }
            }

            val parentNavHostFragment = fragment.findParentNavHostFragment()
            if (parentNavHostFragment?.isCurrentStartDestinationFragment() == true) {
                val parentFragment = parentNavHostFragment.parentFragment
                if (parentFragment is NavMovement) {
                    if (parentFragment.isTransition()) {
                        return false
                    }
                    // gets result from main nav host fragment
                    results = parentFragment.onNavigateUp()?.apply {
                        if (BundleUtil.isNavigationUpBlocked(this)) {
                            results = null
                            return false
                        }
                        getCurrentDestination()?.let {
                            putInt(NavMovement.NAV_UP_FROM_ID, it.id)
                        }
                    }
                    return true
                } else {
                    return false
                }
            }
        }

        return true
    }

//
//    fun finishAndResults(bundle: Bundle?) {
//        results = bundle
//        navController?.currentDestination?.let {
//            results?.putInt(NavDestination.NAV_UP_FROM_ID, it.id)
//        }
//        if(!navigateUp()) {
//            navFinish(bundle)
//        }
//    }
//
//    fun navFinish(bundle: Bundle?) {
//        if (bundle != null) {
//            val intent = Intent()
//            intent.putExtra(NavDestination.NAV_BUNDLE, bundle)
//            setResult(
//                if (bundle.getBoolean(NavDestination.NAV_UP_RESULT_OK, false)) RESULT_OK else RESULT_CANCELED,
//                intent
//            )
//        }
//
//        runOnUiThread { finishAfterTransition() }
//    }
}
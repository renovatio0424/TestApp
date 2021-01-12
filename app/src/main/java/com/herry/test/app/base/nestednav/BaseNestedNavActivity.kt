package com.herry.test.app.base.nestednav

import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.herry.libs.widget.extension.findParentNavHostFragment
import com.herry.test.app.base.nav.BaseNavActivity

abstract class BaseNestedNavActivity: BaseNavActivity() {

    fun addSubNavHostFragment(subNavHostFragment: NavHostFragment?) {
        addOnBackStackChangedListener(subNavHostFragment)
    }

    override fun getCurrentFragment(): Fragment? {
        val fragments = navHostFragment?.childFragmentManager?.fragments ?: mutableListOf()
        for (fragment in fragments) {
            val navHostFragment = findNavHostFragment(fragment)
            if (navHostFragment?.childFragmentManager?.fragments?.isNotEmpty() == true) {
                return navHostFragment.childFragmentManager.fragments[0]
            }
        }

        if (fragments.isNotEmpty()) {
            return fragments[0]
        }
        return null
    }

    override fun getCurrentDestination(): NavDestination? {
        val navHostFragment = findParentNavHostFragment(getCurrentFragment())
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
        val fragments = navHostFragment?.childFragmentManager?.fragments ?: mutableListOf()
        for (fragment in fragments) {
            val navHostFragment = findNavHostFragment(fragment)
            if (navHostFragment?.childFragmentManager?.fragments?.isNotEmpty() == true) {
                return navHostFragment.navController.navigateUp()
            }
        }

        return navHostFragment?.navController?.navigateUp() ?: false
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
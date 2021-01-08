@file:Suppress("unused")

package com.herry.libs.widget.extension

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

fun Fragment.getNavCurrentDestinationID(): Int = findNavController().currentDestination?.id ?: 0

fun Fragment.addNestedNavHostFragment(@IdRes containerViewId: Int, navHostFragment: NavHostFragment?, tag: String? = null, listener: ((requestKey: String, bundle: Bundle) -> Unit)? = null): Boolean {
    navHostFragment ?: return false
    childFragmentManager.beginTransaction()
        .replace(containerViewId, navHostFragment, tag)
        .setPrimaryNavigationFragment(navHostFragment) // this is the equivalent to app:defaultNavHost="true"
        .commit()

    if (listener != null) {
        childFragmentManager.setFragmentResultListener(navHostFragment.id.toString(), this, listener)
    }

    return true
}

fun Fragment.addNestedNavHostFragment(@IdRes containerViewId: Int, @NavigationRes graphResId: Int, startDestinationArgs: Bundle? = null, tag: String? = null, listener: ((requestKey: String, bundle: Bundle) -> Unit)? = null): NavHostFragment {
    val navHostFragment = NavHostFragment.create(graphResId, startDestinationArgs)

    addNestedNavHostFragment(containerViewId, navHostFragment, tag, listener)

    return navHostFragment
}

fun Fragment.setNestedNavHostFragmentResultListener(navHostFragment: NavHostFragment, listener: ((requestKey: String, bundle: Bundle) -> Unit)) {
    navHostFragment.parentFragment?.childFragmentManager?.setFragmentResultListener(navHostFragment.id.toString(), this, listener)
}

private fun findParentNavHostFragment(fragment: Fragment?): NavHostFragment? {
    fragment ?: return null

    var parentFragment: Fragment? = fragment.parentFragment
    while (parentFragment != null) {
        if (parentFragment is NavHostFragment) {
            return parentFragment
        }

        parentFragment = parentFragment.parentFragment
    }

    return null
}

fun Fragment.setNestedNavFragmentResult(result: Bundle) {
    val navHostFragment = findParentNavHostFragment(this)

    navHostFragment?.parentFragment?.childFragmentManager?.setFragmentResult(
        navHostFragment.id.toString(), result
    )
}

fun Fragment.findNestedNavHostFragment(@IdRes id: Int): NavHostFragment? {
    return childFragmentManager.findFragmentById(id) as? NavHostFragment
}
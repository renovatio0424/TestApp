@file:Suppress("unused")

package com.herry.libs.widget.extension

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.herry.libs.util.BundleUtil

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

fun Fragment.addNestedNavHostFragment(
    @IdRes containerViewId: Int,
    @NavigationRes graphResId: Int,
    startDestinationArgs: Bundle? = null,
    tag: String? = null,
    listener: ((requestKey: String, bundle: Bundle) -> Unit)? = null
): NavHostFragment {
    val navHostFragment = NavHostFragment.create(graphResId, startDestinationArgs)

    addNestedNavHostFragment(containerViewId, navHostFragment, tag, listener)

    return navHostFragment
}

fun Fragment.setOnNavNotifyListener(navHostFragment: NavHostFragment, listener: ((requestKey: String, bundle: Bundle) -> Unit)) {
    navHostFragment.parentFragment?.childFragmentManager?.setFragmentResultListener(navHostFragment.id.toString(), this, listener)
}

fun findParentNavHostFragment(fragment: Fragment?): NavHostFragment? {
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

fun Fragment.findNestedNavHostFragment(@IdRes id: Int): NavHostFragment? {
    return childFragmentManager.findFragmentById(id) as? NavHostFragment
}

fun Fragment.popTo(@IdRes destinationId: Int, bundle: Bundle) {
    findNavController().run {
        popBackStack(destinationId, true)
        notifyTo(destinationId, bundle)
    }
}

fun Fragment.popToNavHost(bundle: Bundle? = null) {
    val navHostFragment = if (this is NavHostFragment) {
        this
    } else {
        findParentNavHostFragment(this)
    } ?: return

    val destinationId = navHostFragment.findNavController().graph.startDestination

    findNavController().run {
        popBackStack(destinationId, false)
        notifyTo(destinationId, bundle ?: BundleUtil.createNavigationBundle(false))
    }
}

fun Fragment.notifyTo(@IdRes destinationId: Int, bundle: Bundle) {
    setFragmentResult(destinationId.toString(), bundle)
}

fun Fragment.notifyToNestedNavHost(bundle: Bundle) {
    val navHostFragment = findParentNavHostFragment(this)

    navHostFragment?.parentFragment?.childFragmentManager?.setFragmentResult(
        navHostFragment.id.toString(), bundle
    )
}

fun Fragment.notifyToCaller(bundle: Bundle) {
    notifyTo(getNavCurrentDestinationID(), bundle)
}

fun Fragment.notifyToCurrent(bundle: Bundle) {
    val currentDestinationId = findNavController().currentBackStackEntry?.destination?.id
    if (currentDestinationId != null) {
        notifyTo(currentDestinationId, bundle)
    }
}

fun Fragment.navigate(@IdRes resId: Int, onResult: ((bundle: Bundle) -> Unit)? = null) {
    navigate(resId, null, onResult)
}

fun Fragment.navigate(@IdRes resId: Int, args: Bundle?, onResult: ((bundle: Bundle) -> Unit)? = null) {
    navigate(resId, args, null, onResult)
}

fun Fragment.navigate(@IdRes resId: Int, args: Bundle?, navOptions: NavOptions?, onResult: ((bundle: Bundle) -> Unit)? = null) {
    navigate(resId, args, navOptions, null, onResult)
}

/**
 * Navigate via the given [NavDirections]
 *
 * @param directions directions that describe this navigation operation
 */
fun Fragment.navigate(directions: NavDirections, onResult: ((bundle: Bundle) -> Unit)? = null) {
    navigate(directions.actionId, directions.arguments, onResult)
}

/**
 * Navigate via the given [NavDirections]
 *
 * @param directions directions that describe this navigation operation
 * @param navOptions special options for this navigation operation
 */
fun Fragment.navigate(directions: NavDirections, navOptions: NavOptions?, onResult: ((bundle: Bundle) -> Unit)? = null) {
    navigate(directions.actionId, directions.arguments, navOptions, onResult)
}

/**
 * Navigate via the given [NavDirections]
 *
 * @param directions directions that describe this navigation operation
 * @param navigatorExtras extras to pass to the [Navigator]
 */
fun Fragment.navigate(directions: NavDirections, navigatorExtras: Navigator.Extras, onResult: ((bundle: Bundle) -> Unit)? = null) {
    navigate(directions.actionId, directions.arguments, null, navigatorExtras, onResult)
}

/**
 * Navigate to a destination from the current navigation graph. This supports both navigating
 * via an {@link NavDestination#getAction(int) action} and directly navigating to a destination.
 *
 * @param resId an {@link NavDestination#getAction(int) action} id or a destination id to
 *              navigate to
 * @param args arguments to pass to the destination
 * @param navOptions special options for this navigation operation
 * @param navigatorExtras extras to pass to the Navigator
 */
fun Fragment.navigate(@IdRes resId: Int, args: Bundle?, navOptions: NavOptions?, navigatorExtras: Navigator.Extras?, onResult: ((bundle: Bundle) -> Unit)? = null) {
    findNavController().navigate(resId, args, navOptions, navigatorExtras)

    if (onResult != null) {
        val backStack = findNavController().currentBackStackEntry ?: return
        val navDestination: NavDestination = backStack.destination
        @IdRes val destId: Int = navDestination.id
        if (destId == 0) return

        val navHostFragment = findParentNavHostFragment(this)
        if (navHostFragment != null) {
            this.setFragmentResultListener(destId.toString()) { _: String, bundle: Bundle ->
                onResult(bundle)
            }
        }
    }
}
package com.herry.test.app.base.nav

import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.herry.libs.app.nav.NavMovement
import com.herry.test.R
import com.herry.test.app.base.BaseActivity

abstract class BaseNavActivity: BaseActivity() {

    protected var navController: NavController? = null

    var navHostFragment: NavHostFragment? = null
        private set

    protected var results: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())

        navHostFragment = supportFragmentManager.findFragmentById(getNavHostFragment()) as? NavHostFragment
        addOnBackStackChangedListener(navHostFragment)

        navController = Navigation.findNavController(this, getNavHostFragment())
        navController?.let {
            val navGraph = it.navInflater.inflate(getGraph())
            if (getStartDestination() != 0) {
                navGraph.startDestination = getStartDestination()
            }
            it.setGraph(navGraph, getDefaultBundle())
        }
    }

    protected fun addOnBackStackChangedListener(navHostFragment: NavHostFragment?) {
        navHostFragment?.childFragmentManager?.addOnBackStackChangedListener {
            results?.let {
                val fragment: Fragment? = getCurrentFragment()
                if (fragment is NavMovement) {
                    val navDestination = navHostFragment.navController.currentDestination
                    if (navDestination != null) {
                        if (it.getInt(NavMovement.NAV_UP_DES_ID, 0) != 0 && it.getInt(
                                NavMovement.NAV_UP_DES_ID, 0) != navController?.currentDestination?.id) {
                            if (!navigateUp()) {
                                navFinish(it)
                            }
                        } else {
                            results = null
                        }
                    }
                }
            }
        }
    }

    @LayoutRes
    protected open fun getContentView(): Int = R.layout.activity_navigation
    @IdRes
    protected open fun getNavHostFragment(): Int  = R.id.activity_navigation_fragment

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val bundle = getDefaultBundle()
        if (bundle != null) {
            navController?.setGraph(getGraph(), bundle)
        } else {
            navController?.setGraph(getGraph())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (navigateUpAndResults()) {
            return navigateUp()
        }
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (navigateUpAndResults() && !navigateUp()) {
            navFinish(results)
        }
    }

    protected open fun navigateUp(): Boolean = navController?.navigateUp() ?: false

    protected open fun navigateUpAndResults(): Boolean {
        val fragment = getCurrentFragment()
        if (fragment is NavMovement) {
            if (fragment.isTransition()) {
                return false
            }

            results = fragment.onNavigateUp()?.apply {
                if (getBoolean(NavMovement.NAV_UP_BLOCK, false)) {
                    results = null
                    return false
                }
                getCurrentDestination()?.let {
                    putInt(NavMovement.NAV_UP_FROM_ID, it.id)
                }
            }
        }

        return true
    }

    protected open fun getCurrentDestination(): NavDestination? = navController?.currentDestination

    protected open fun getCurrentFragment(): Fragment? {
        val list = navHostFragment?.childFragmentManager?.fragments
        if (list?.isNotEmpty() == true) {
            return list[0]
        }
        return null
    }

    abstract fun getGraph(): Int

    protected fun getDefaultBundle(): Bundle? {
        return if (intent != null) intent.getBundleExtra(NavMovement.NAV_BUNDLE) else null
    }

    protected fun getStartDestination(): Int {
        return if (intent != null) intent.getIntExtra(NavMovement.NAV_START_DESTINATION, 0) else 0
    }

    fun finishAndResults(bundle: Bundle?) {
        results = bundle
        navController?.currentDestination?.let {
            results?.putInt(NavMovement.NAV_UP_FROM_ID, it.id)
        }
        if(!navigateUp()) {
            navFinish(bundle)
        }
    }

    fun navFinish(bundle: Bundle?) {
        if (bundle != null) {
            val intent = Intent()
            intent.putExtra(NavMovement.NAV_BUNDLE, bundle)
            setResult(
                if (bundle.getBoolean(NavMovement.NAV_UP_RESULT_OK, false)) RESULT_OK else RESULT_CANCELED,
                intent
            )
        }

        runOnUiThread { finishAfterTransition() }
    }
}
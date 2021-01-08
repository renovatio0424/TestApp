package com.herry.test.app.base.nav

import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.herry.libs.app.nav.NavDestination
import com.herry.test.R
import com.herry.test.app.base.BaseActivity

abstract class BaseNavActivity: BaseActivity() {

    protected var navController: NavController? = null

    protected var navHostFragment: NavHostFragment? = null

    private var results: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())

        navHostFragment = supportFragmentManager.findFragmentById(getNavHostFragment()) as? NavHostFragment
        navHostFragment?.childFragmentManager?.addOnBackStackChangedListener {
            results?.let {
                val fragment: Fragment? = getCurrentFragment()
                if(fragment is NavDestination) {
                    if (navController?.currentDestination != null) {
                        if (it.getInt(NavDestination.NAV_UP_DES_ID, 0) != 0 && it.getInt(
                                NavDestination.NAV_UP_DES_ID, 0) != navController?.currentDestination?.id) {
                            if (!navigateUp()) {
                                navFinish(it)
                            }
                        } else {
                            fragment.onNavResults(it)
                            results = null
                        }
                    }
                }
            }
        }

        navController = Navigation.findNavController(this, getNavHostFragment())
        navController?.let {
            val navGraph = it.navInflater.inflate(getGraph())
            if(getStartDestination() != 0) {
                navGraph.startDestination = getStartDestination()
            }
            it.setGraph(navGraph, getDefaultBundle())
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
        if(upAndResults()) {
            return navigateUp()
        }
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if(upAndResults() && !navigateUp()) {
            navFinish(results)
        }
    }

    private fun navigateUp(): Boolean = navController?.navigateUp() ?: false

    private fun upAndResults(): Boolean {
        val fragment = getCurrentFragment()
        if (fragment is NavDestination) {
            if(fragment.isTransition()) {
                return false
            }

            results = fragment.onNavUp()
            results?.let { _result ->
                if(_result.getBoolean(NavDestination.NAV_UP_BLOCK, false)) {
                    results = null
                    return false
                }
                navController?.currentDestination?.let {
                    _result.putInt(NavDestination.NAV_UP_FROM_ID, it.id)
                }
            }
        }

        return true
    }

    protected fun getCurrentFragment(): Fragment? {
        val list = navHostFragment?.childFragmentManager?.fragments
        if (list?.isNotEmpty() == true) {
            return list[0]
        }
        return null
    }

    abstract fun getGraph(): Int

    protected fun getDefaultBundle(): Bundle? {
        return if (intent != null) intent.getBundleExtra(NavDestination.NAV_BUNDLE) else null
    }

    protected fun getStartDestination(): Int {
        return if (intent != null) intent.getIntExtra(NavDestination.NAV_START_DESTINATION, 0) else 0
    }

    fun finishAndResults(bundle: Bundle?) {
        results = bundle
        navController?.currentDestination?.let {
            results?.putInt(NavDestination.NAV_UP_FROM_ID, it.id)
        }
        if(!navigateUp()) {
            navFinish(bundle)
        }
    }

    fun navFinish(bundle: Bundle?) {
        if (bundle != null) {
            val intent = Intent()
            intent.putExtra(NavDestination.NAV_BUNDLE, bundle)
            setResult(
                if (bundle.getBoolean(NavDestination.NAV_UP_RESULT_OK, false)) RESULT_OK else RESULT_CANCELED,
                intent
            )
        }

        runOnUiThread { finishAfterTransition() }
    }
}
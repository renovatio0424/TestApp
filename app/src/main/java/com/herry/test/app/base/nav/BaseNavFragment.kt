package com.herry.test.app.base.nav

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.herry.libs.app.nav.NavBundleUtil
import com.herry.libs.app.nav.NavMovement
import com.herry.libs.util.BundleUtil
import com.herry.libs.util.ViewUtil
import com.herry.test.app.base.BaseActivity
import com.herry.test.app.base.BaseFragment

@Suppress("SameParameterValue", "KDocUnresolvedReference")
open class BaseNavFragment : BaseFragment(), NavMovement {

    /**
     * {@inheritDoc}
     *
     * @deprecated use
     * {@link #onNavigateUp()}
     */
    final override fun onBackPressed(): Boolean = false

    override fun onNavigateUp(): Bundle {
        return NavBundleUtil.createNavigationBundle(false)
    }

    override fun onNavigateResults(from: Int, result: Bundle) {}

    protected open fun navigateUp(resultOK: Boolean = false, result: Bundle? = null) {
        navigateUp(NavBundleUtil.createNavigationBundle(resultOK, result))
    }

    protected open fun navigateUp(bundle: Bundle? = null) {
        try {
            setNavigateResults(bundle)
            if (!findNavController().navigateUp()) {
                finishAndResults(bundle)
            }
        } catch (ex: IllegalStateException) {
            finishAndResults(bundle)
        }
    }

    @Throws(IllegalStateException::class)
    protected fun setNavigateResults(bundle: Bundle?) {
        val currentDestinationId = findNavController().currentBackStackEntry?.destination?.id
        if (currentDestinationId != null) {
            NavBundleUtil.addFromNavigationId(bundle, currentDestinationId)

            if (activity is BaseNavActivity) {
                (activity as BaseNavActivity).setNavigationUpResult(bundle ?: NavBundleUtil.createNavigationBundle(false))
            }
        }
    }

    override fun isTransition(): Boolean = transitionHelper.isTransition()

    /**
     * finish fragment.
     * If you want finish with to set result, creates [bundle] parameter.
     * @see BundleUtil.createNavigationBundle(Boolean)
     *
     * - Sets result to RESULT_OK
     *   finishActivity(BundleUtil.createNavigationBundle(true))
     * - Sets result to RESULT_CANCEL
     * finishActivity(null) or finishActivity(BundleUtil.createNavigationBundle(false))
     * @param bundle result data
     */
    protected fun finishAndResults(bundle: Bundle?) {
        val activity = this.activity
        if (activity is BaseNavActivity) {
            activity.window?.let {
                ViewUtil.hideSoftKeyboard(context, activity.window.decorView.rootView)
            }
            activity.finishAndResults(bundle)
        } else if (activity is BaseActivity) {
            finishAndResults(NavBundleUtil.isNavigationResultOk(bundle), bundle)
        }
    }

    /**
     * finish fragment.
     * If you want finish with to set result, creates [bundle] parameter.
     * @see BundleUtil.createNavigationBundle(Boolean)
     * @param resultOK set result to ok or cancel
     * @param bundle result data
     */
    protected open fun finishAndResults(resultOK: Boolean, bundle: Bundle? = null) {
        activity?.let { activity ->
            activity.window?.let {
                ViewUtil.hideSoftKeyboard(context, activity.window.decorView.rootView)
            }

            val activityResult = if (resultOK) Activity.RESULT_OK else Activity.RESULT_CANCELED
            val resultBundle = if (null != bundle) {
                bundle.putBoolean(NavMovement.NAV_UP_RESULT_OK, resultOK)
                bundle
            } else {
                Bundle().apply {
                    putBoolean(NavMovement.NAV_UP_RESULT_OK, resultOK)
                }
            }
            activity.setResult(activityResult, Intent().apply {
                putExtra(NavMovement.NAV_BUNDLE, resultBundle)
            })
            activity.finishAfterTransition()
        }
    }
}
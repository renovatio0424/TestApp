package com.herry.test.app.base.nav

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.TransitionRes
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.herry.libs.app.nav.NavMovement
import com.herry.libs.helper.TransitionHelper
import com.herry.libs.util.BundleUtil
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.extension.isNavHostFragment
import com.herry.libs.widget.extension.setNotifyListenerFromChild
import com.herry.test.app.base.BaseActivity
import com.herry.test.app.base.BaseFragment

@Suppress("SameParameterValue")
open class BaseNavFragment: BaseFragment(), NavMovement {

    /**
     * {@inheritDoc}
     *
     * @deprecated use
     * {@link #onNavigateUp()}
     */
    final override fun onBackPressed(): Boolean = false

    final override fun onNavigateUp(): Bundle {
        val bundle = onNavigateUpResult() ?: BundleUtil.createNavigationBundle(false)

        val currentDestinationId = findNavController().currentBackStackEntry?.destination?.id
        if (currentDestinationId != null) {
            setFragmentResult(currentDestinationId.toString(), bundle)
        }

        return bundle
    }

    protected open fun navigateUpAndResult(resultOK: Boolean) {
        navigateUpAndResult(BundleUtil.createNavigationBundle(resultOK))
    }

    protected open fun navigateUpAndResult(bundle: Bundle?) {
        try {
            val currentDestinationId = findNavController().currentBackStackEntry?.destination?.id
            if (currentDestinationId != null) {
                setFragmentResult(currentDestinationId.toString(), bundle ?: BundleUtil.createNavigationBundle(false))
            }
            if (!navigateUp()) {
                finishAndResults(bundle)
            }
        } catch (ex: IllegalStateException) {
            finishAndResults(bundle)
        }
    }

    protected open fun onNavigateUpResult(): Bundle? = null

    override fun isTransition(): Boolean = transitionHelper.isTransition()

    protected open fun onNotifiedFromChild(from: String, bundle: Bundle) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        transitionHelper.onCreate(activity, this)

        // checks current fragment is NavHostFragment or not
        this.isNavHostFragment()?.run {
            setNotifyListenerFromChild { from, bundle ->
                onNotifiedFromChild(from, bundle)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        transitionHelper.onDestroy(activity)
    }

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
        if(activity is BaseNavActivity) {
            activity.window?.let {
                ViewUtil.hideSoftKeyboard(context, activity.window.decorView.rootView)
            }
            activity.finishAndResults(bundle)
        } else if (activity is BaseActivity) {
            finishAndResults(BundleUtil.isNavigationResultOk(bundle), bundle)
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

    fun navigateUp(): Boolean = findNavController().navigateUp()

    private val transitionHelper by lazy {
        TransitionHelper(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            listener = object : TransitionHelper.TransitionHelperListener {
                override fun onTransitionStart() {
                    this@BaseNavFragment.onTransitionStart()
                }

                override fun onTransitionEnd() {
                    this@BaseNavFragment.onTransitionEnd()
                }
            }
        )
    }

    @TransitionRes
    protected open val enterTransition: Int = 0

    @TransitionRes
    protected open val exitTransition: Int = 0

    protected open fun onTransitionStart() {

    }

    protected open fun onTransitionEnd() {

    }
}
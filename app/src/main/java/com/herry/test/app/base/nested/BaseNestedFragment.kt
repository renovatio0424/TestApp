package com.herry.test.app.base.nested

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.herry.libs.app.nav.NavMovement
import com.herry.libs.util.AppUtil
import com.herry.libs.util.ViewUtil
import com.herry.test.app.base.BaseFragment

open class BaseNestedFragment: BaseFragment() {

    final override fun setResult(result: Bundle?) {
        this.parentFragment?.let { parentFragment ->
            parentFragment.childFragmentManager.setFragmentResult(fragmentTag, result ?: bundleOf())
            return@setResult
        }
        super.setResult(result)
    }

    protected fun setResult(resultOK: Boolean, bundle: Bundle?) {
        setResult(if (null != bundle) {
            bundle.putBoolean(NavMovement.NAV_UP_RESULT_OK, resultOK)
            bundle
        } else {
            Bundle().apply {
                putBoolean(NavMovement.NAV_UP_RESULT_OK, resultOK)
            }
        })
    }

    /**
     * finish fragment.
     * If you want finish with to set result, creates [bundle] parameter.
     * @see com.herry.libs.util.BundleUtil.createNavigationBundle(Boolean)
     * @param resultCode set result to ok or cancel
     * @param bundle result data
     */
    protected open fun finishAndResults(resultOK: Boolean, bundle: Bundle? = null) {
        this.parentFragment?.let { parentFragment ->
            val backStackFragment = AppUtil.getLastBackStackFragment(parentFragment.childFragmentManager, true)

            // set fragment result
            setResult(resultOK, bundle)

            if (backStackFragment?.fragment == this) {
                // pop latest fragment
                parentFragment.childFragmentManager.popBackStack()
            } else {
                parentFragment.childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                parentFragment.childFragmentManager.popBackStack(this.tag, 0);
//                // remove fragment from transition
//                parentFragment.childFragmentManager
//                    .beginTransaction()
//                    .remove(this)
//                    .commit()
            }
            return@finishAndResults
        }

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
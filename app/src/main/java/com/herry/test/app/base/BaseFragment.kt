package com.herry.test.app.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.herry.libs.app.activity_caller.AC
import com.herry.libs.app.nav.NavDestination
import com.herry.libs.helper.ApiHelper
import com.herry.libs.util.AppUtil
import com.herry.libs.util.FragmentAddingOption
import com.herry.libs.util.ViewUtil

open class BaseFragment: Fragment() {
    internal open var activityCaller: AC? = null

    internal val fragmentTag: String = createTag()

    companion object {
        private const val TAG = "ARG_TAG"
    }

    private fun createTag(): String = "${this::class.java.simpleName}#${System.currentTimeMillis()}"

    protected open fun createArguments(): Bundle = bundleOf(TAG to fragmentTag)

    protected fun getDefaultArguments(): Bundle {
        return arguments ?: Bundle()
    }

    fun setDefaultArguments(bundle: Bundle) {
        setArguments(bundle)
    }

    @Suppress("DEPRECATION")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        if (ApiHelper.hasMarshmallow()) {
            return
        }

        onAttachToContext(activity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (ApiHelper.hasMarshmallow()) {
            onAttachToContext(context)
        }
    }

    protected open fun onAttachToContext(context: Context?) {
        activityCaller = if(context is AC) {
            context
        } else {
            null
        }
    }

    override fun onDetach() {
        super.onDetach()
        activityCaller = null
    }

    open fun onBackPressed(): Boolean = false

    /**
     * finish fragment.
     * If you want finish with to set result, creates [bundle] parameter.
     * @see com.herry.libs.util.BundleUtil.createNavigationBundle(Boolean)
     * @param resultCode set result to ok or cancel
     * @param bundle result data
     */
    protected open fun finishAndResults(resultOK: Boolean, bundle: Bundle? = null) {
        activity?.let { activity ->
            activity.window?.let {
                ViewUtil.hideSoftKeyboard(context, activity.window.decorView.rootView)
            }

            val activityResult = if (resultOK) Activity.RESULT_OK else Activity.RESULT_CANCELED
            val resultBundle = if (null != bundle) {
                bundle.putBoolean(NavDestination.NAV_UP_RESULT_OK, resultOK)
                bundle
            } else {
                Bundle().apply {
                    putBoolean(NavDestination.NAV_UP_RESULT_OK, resultOK)
                }
            }
            activity.setResult(activityResult, Intent().apply {
                putExtra(NavDestination.NAV_BUNDLE, resultBundle)
            })
            activity.finishAfterTransition()
        }
    }

    protected open fun setResultListener(
        fragmentManager: FragmentManager?,
        requestKey: String,
        listener: ((resultKey: String, bundle: Bundle) -> Unit)
    ) {
        fragmentManager ?: return

        fragmentManager.setFragmentResultListener(requestKey, this, listener)
    }

    protected open fun setResult(result: Bundle?) {
        parentFragmentManager.setFragmentResult(
            fragmentTag,
            result ?: bundleOf()
        )
    }

    protected fun addChildFragment(
        @IdRes containerViewId: Int?,
        fragment: Fragment?,
        option: FragmentAddingOption,
        listener: ((resultKey: String, bundle: Bundle) -> Unit)? = null
    ): Boolean {
        if (containerViewId == null || containerViewId == -1) return false
        fragment ?: return false
        val fragmentManager: FragmentManager = childFragmentManager

        // sets result listener
        if (listener != null) {
            val requestKey = option.tag ?: run {
                if (fragment is BaseFragment) {
                    fragment.fragmentTag
                } else {
                    tag
                }
            }

            if (requestKey.isNullOrBlank()) {
                throw IllegalArgumentException("Must set option.tag")
            }

            setResultListener(fragmentManager, requestKey, listener)
        }

        return AppUtil.setFragment(fragmentManager, containerViewId, fragment, option)
    }

    protected fun addFragment(
        fragment: Fragment?,
        option: FragmentAddingOption = FragmentAddingOption(isReplace = false),
        listener: ((resultKey: String, bundle: Bundle) -> Unit)? = null
    ): Boolean {
        val activity = requireActivity()
        if (activity !is BaseActivity) {
            return false
        }

        val containerViewId = activity.getHostViewID()
        if (containerViewId == null || containerViewId == -1) return false

        fragment ?: return false
        val fragmentManager: FragmentManager = parentFragmentManager

        // sets result listener
        if (listener != null) {
            val requestKey = option.tag ?: run {
                if (fragment is BaseFragment) {
                    fragment.fragmentTag
                } else {
                    tag
                }
            }

            if (requestKey.isNullOrBlank()) {
                throw IllegalArgumentException("Must set option.tag")
            }

            setResultListener(fragmentManager, requestKey, listener)
        }

        return AppUtil.setFragment(fragmentManager, containerViewId, fragment, option)
    }
}
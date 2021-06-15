package com.herry.test.app.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.herry.libs.app.activity_caller.AC
import com.herry.libs.helper.ApiHelper
import com.herry.libs.util.AppUtil
import com.herry.libs.util.FragmentAddingOption
import com.herry.libs.widget.view.LoadingCountView

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

    protected open fun setResultListener(
        fragmentManager: FragmentManager?,
        requestKey: String,
        listener: ((resultKey: String, bundle: Bundle) -> Unit)
    ) {
        fragmentManager ?: return

        fragmentManager.setFragmentResultListener(requestKey, this, listener)
    }

    protected open fun setResult(result: Bundle?) {
        setFragmentResult(fragmentTag, result ?: bundleOf())
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

    protected fun addFragmentToActivity(
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

        val fragmentManager: FragmentManager = activity.supportFragmentManager

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

    private var loading: LoadingCountView? = null

    protected open fun showLoading() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showMainLoopLoading()
        } else {
            Handler(Looper.getMainLooper()).post {
                showMainLoopLoading()
            }
        }
    }

    private fun showMainLoopLoading() {
        if (loading == null) {
            loading = context?.run {
                LoadingCountView(this).apply {
                }
            }

            loading?.let {
                it.visibility = View.GONE

                if (view is FrameLayout) {
                    (view as FrameLayout).addView(it, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                } else if (view is ConstraintLayout) {
                    val layoutParams = ConstraintLayout.LayoutParams(0, 0).apply {
                        startToStart = ConstraintSet.PARENT_ID
                        endToEnd = ConstraintSet.PARENT_ID
                        topToTop = ConstraintSet.PARENT_ID
                        bottomToBottom = ConstraintSet.PARENT_ID
                    }
                    (view as ConstraintLayout).addView(it, layoutParams)
                }
            }
        }

        loading?.show()
    }

    protected open fun hideLoading() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            loading?.hide()
        } else {
            Handler(Looper.getMainLooper()).post {
                loading?.hide()
            }
        }
    }
}
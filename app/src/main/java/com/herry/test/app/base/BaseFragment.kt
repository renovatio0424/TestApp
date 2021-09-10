package com.herry.test.app.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.TransitionRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.herry.libs.app.activity_caller.AC
import com.herry.libs.helper.ApiHelper
import com.herry.libs.helper.TransitionHelper
import com.herry.libs.widget.view.LoadingCountView

open class BaseFragment: Fragment() {
    internal open var activityCaller: AC? = null

    private val fragmentTag: String = createTag()

    companion object {
        private const val TAG = "ARG_TAG"
    }

    private fun createTag(): String = "${this::class.java.simpleName}#${System.currentTimeMillis()}"

    protected open fun createArguments(): Bundle = bundleOf(TAG to fragmentTag)

    protected fun getDefaultArguments(): Bundle {
        return arguments ?: Bundle()
    }

    fun setDefaultArguments(bundle: Bundle) {
        this.arguments = bundle
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        transitionHelper.onCreate(activity, this)
    }

    override fun onDestroy() {
        super.onDestroy()

        transitionHelper.onDestroy(activity)
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

    protected val transitionHelper by lazy {
        TransitionHelper(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            listener = object : TransitionHelper.TransitionHelperListener {
                override fun onTransitionStart() {
                    this@BaseFragment.onTransitionStart()
                }

                override fun onTransitionEnd() {
                    this@BaseFragment.onTransitionEnd()
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
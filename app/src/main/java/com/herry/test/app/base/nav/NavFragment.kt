package com.herry.test.app.base.nav

import android.content.Intent
import android.os.Bundle
import androidx.annotation.TransitionRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.herry.libs.helper.TransitionHelper
import com.herry.test.app.base.BaseActivity
import com.herry.test.app.base.BaseFragment

open class NavFragment: BaseFragment(), NavDestination {
    override fun onNavUp(): Bundle? = null

    override fun onNavResults(bundle: Bundle) {
    }

    override fun isTransition(): Boolean = transitionHelper.isTransition()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        transitionHelper.onCreate(activity, this)
    }

    override fun onDestroy() {
        super.onDestroy()

        transitionHelper.onDestroy(activity)
    }

    protected fun finishAndResults(bundle: Bundle?) {
        val activity = this.activity
        if(activity is NavActivity) {
//            activity.window?.let {
//                ViewUtil.hideSoftKeyboard(context, activity.window.decorView.rootView)
//            }
            activity.finishAndResults(bundle)
        } else if (activity is BaseActivity) {
            finishAndResultsAndNav(bundle)
        }
    }

    protected fun finishAndResultsAndNav(bundle: Bundle?) {
        activity?.let { activity ->
//            activity.window?.let {
//                ViewUtil.hideSoftKeyboard(context, activity.window.decorView.rootView)
//            }

            bundle?.let {
                val intent = Intent()
                intent.putExtra(NavDestination.NAV_BUNDLE, it)
                activity.setResult(
                    if (it.getBoolean(NavDestination.NAV_UP_RESULT_OK, false)) AppCompatActivity.RESULT_OK else AppCompatActivity.RESULT_CANCELED,
                    intent
                )
                activity.finishAfterTransition()
            } ?: activity.finishAfterTransition()
        }
    }

    protected fun navController(): NavController? {
        val view = this.view ?: return null

        return Navigation.findNavController(view)
    }

    private val transitionHelper by lazy {
        TransitionHelper(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            listener = object: TransitionHelper.TransitionHelperListener {
                override fun onTransitionStart() {
                    this@NavFragment.onTransitionStart()
                }

                override fun onTransitionEnd() {
                    this@NavFragment.onTransitionEnd()
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
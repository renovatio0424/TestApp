package com.herry.test.app.base.nested

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.herry.libs.app.nav.NavDestination
import com.herry.libs.util.AppUtil
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
            bundle.putBoolean(NavDestination.NAV_UP_RESULT_OK, resultOK)
            bundle
        } else {
            Bundle().apply {
                putBoolean(NavDestination.NAV_UP_RESULT_OK, resultOK)
            }
        })
    }

    override fun finishAndResults(resultOK: Boolean, bundle: Bundle?) {
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
        super.finishAndResults(resultOK, bundle)
    }
}
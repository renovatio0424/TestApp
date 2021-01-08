package com.herry.test.app.base.nested

import com.herry.libs.util.AppUtil
import com.herry.test.app.base.BaseActivity

open class BaseNestedActivity: BaseActivity() {

    override fun onBackPressed() {
        val backStackFragment = AppUtil.getLastBackStackFragment(supportFragmentManager, true)
        if (null != backStackFragment && backStackFragment.fragment is BaseNestedFragment) {
            val fragmentManager = backStackFragment.fragmentManager
            val fragment = backStackFragment.fragment as BaseNestedFragment
            val isChildFragment = backStackFragment.isChild

            if (fragment.onBackPressed()) {
                return
            }

            if (isChildFragment) {
                fragmentManager.popBackStack()
                return
            }
        }

        super.onBackPressed()
    }
}
package com.herry.test.app.base.nested

import com.herry.libs.util.AppUtil
import com.herry.test.app.base.BaseActivity

open class NestedActivity: BaseActivity() {

    override fun onBackPressed() {
        val backStackFragment = AppUtil.getLastBackStackFragment(supportFragmentManager, true)
        if (null != backStackFragment && backStackFragment.fragment is NestedFragment) {
            val fragmentManager = backStackFragment.fragmentManager
            val fragment = backStackFragment.fragment as NestedFragment
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
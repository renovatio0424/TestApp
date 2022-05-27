package com.herry.test.app.sample.home.form

import android.content.Context
import android.os.Bundle
import android.view.View
import com.herry.libs.app.nav.BottomNavHostFragment
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.NodeView
import com.herry.libs.widget.extension.findNestedNavHostFragment
import com.herry.libs.widget.extension.setFragmentNotifyListener
import com.herry.libs.widget.extension.setNavigate
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavFragment
import com.herry.test.app.base.nestednav.NestedNavMovement

class HomeBottomNavFragmentForm(
    private val parentNestedNavFragment: BaseNavFragment,
    private val onNavNotify: (bundle: Bundle) -> Unit
) : NodeView<HomeBottomNavFragmentForm.Holder>() {
    inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
        val bottomNavHostFragment: BottomNavHostFragment? = parentNestedNavFragment.findNestedNavHostFragment(R.id.home_bottom_navigator_fragment_container) as? BottomNavHostFragment

        init {
            bottomNavHostFragment?.let { navHostFragment ->
                navHostFragment.setFragmentNotifyListener { _, bundle ->
                    onNavNotify(bundle)
                }
                if (parentNestedNavFragment is NestedNavMovement) {
                    parentNestedNavFragment.addNestedNavHostFragment(navHostFragment)
                }
            }
        }
    }

    override fun onLayout(): Int = R.layout.home_bottom_navigator_fragment_form

    override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

    fun setNavScreen(screenId: HomeBottomNavScreenId, isStart: Boolean, startArgs: Bundle?) {
        val bottomNavHostFragment = holder?.bottomNavHostFragment ?: return
        bottomNavHostFragment.setNavigate(screenId.id, /*NavAnim().apply {
            enterAnim = R.animator.nav_default_enter_anim
            exitAnim = R.animator.nav_default_exit_anim
            popEnterAnim = R.animator.nav_default_pop_enter_anim
            popExitAnim = R.animator.nav_default_pop_exit_anim
        }*/)
    }
}
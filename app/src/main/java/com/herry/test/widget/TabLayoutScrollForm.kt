package com.herry.test.widget

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.google.android.material.tabs.TabLayout
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.NodeView
import com.herry.libs.util.ViewUtil
import com.herry.test.R

class TabLayoutScrollForm: NodeView<TabLayoutScrollForm.Holder>() {

    inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
        val tabLayout: TabLayout? = view.findViewById(R.id.tab_layout_scroll_form_tab_layout)
        val startIcon: View? = view.findViewById(R.id.tab_layout_scroll_form_start)
        val endIcon: View? = view.findViewById(R.id.tab_layout_scroll_form__end)

        var tabLayoutChildWidth = 0
        val gapWidth = ViewUtil.getDimensionPixelSize(context, R.dimen.size10)

        init {
            tabLayout?.viewTreeObserver?.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    tabLayoutChildWidth = tabLayout.getChildAt(0)?.measuredWidth ?: 0
                    if (tabLayoutChildWidth <= tabLayout.width) {
                        tabLayout.tabMode = TabLayout.MODE_FIXED
                    } else {
                        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
                        tabLayout.viewTreeObserver.addOnScrollChangedListener {
                            val scrollX = tabLayout.scrollX
                            if (scrollX > gapWidth) {
                                startIcon?.visibility = View.VISIBLE
                            } else {
                                startIcon?.visibility = View.GONE
                            }
                            if (scrollX < tabLayoutChildWidth - tabLayout.width) {
                                endIcon?.visibility = View.VISIBLE
                            } else {
                                endIcon?.visibility = View.GONE
                            }
                        }
                    }
                    tabLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    override fun onLayout(): Int = R.layout.tab_layout_scroll_form

    override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

    fun getTabLayout(): TabLayout? {
        return holder?.tabLayout
    }

    fun setSelectTab(position: Int, init: Boolean) {
        val tabLayout = getTabLayout()
        if (tabLayout != null) {
            if (init) {
                setSelectTab(position)
                tabLayout.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        setSelectTab(position)
                        tabLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            } else {
                setSelectTab(position)
            }
        }
    }

    private fun setSelectTab(position: Int) {
        val tabLayout = getTabLayout()
        if (tabLayout != null) {
            tabLayout.getTabAt(position)?.select()
        }
    }
}
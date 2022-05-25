package com.herry.test.widget

import android.content.Context
import android.view.View
import com.google.android.material.tabs.TabLayout
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.NodeView
import com.herry.test.R

class TabLayoutForm(
    private val tabMode: TabMode,
    private val gapWidth: Int
): NodeView<TabLayoutForm.Holder>() {

    enum class TabMode {
        FIXED,
        SCROLLABLE,
        AUTO
    }

    data class TabItem(
        val title: String
    )

    data class Model(
        val selected: Int = 0,
        val tabs: MutableList<TabItem> = mutableListOf()
    )

    inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
        val tabLayout: TabLayout? = view.findViewById(R.id.tab_layout_scroll_form_tab_layout)
        private val startIcon: View? = view.findViewById(R.id.tab_layout_scroll_form_start)
        private val endIcon: View? = view.findViewById(R.id.tab_layout_scroll_form_end)

        init {
            tabLayout?.let { tabLayout ->
                tabLayout.tabMode = when (tabMode) {
                    TabMode.FIXED -> TabLayout.MODE_FIXED
                    TabMode.SCROLLABLE -> TabLayout.MODE_SCROLLABLE
                    TabMode.AUTO -> TabLayout.MODE_AUTO
                }
                tabLayout.viewTreeObserver?.addOnScrollChangedListener {
                    val tabLayoutChildWidth = tabLayout.getChildAt(0)?.measuredWidth ?: 0

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
        }
    }

    override fun onLayout(): Int = R.layout.tab_layout_scroll_form

    override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

    fun getTabLayout(): TabLayout? {
        return holder?.tabLayout
    }
//
//
//    override fun onBindModel(context: Context, holder: Holder, model: Model) {
//        holder.tabLayout?.let { tabLayout ->
//            tabLayout.removeAllTabs()
//            model.tabs.forEachIndexed { index, tabItem ->
//                val tab = tabLayout.newTab().setText(tabItem.title)
//                tabLayout.addTab(tab, model.selected == index)
//            }
//        }
//    }
}
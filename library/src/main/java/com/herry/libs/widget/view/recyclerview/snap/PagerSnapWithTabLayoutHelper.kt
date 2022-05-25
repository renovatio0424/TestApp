package com.herry.libs.widget.view.recyclerview.snap

import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class PagerSnapWithTabLayoutHelper(val tabLayout: TabLayout, val snapHelper: PagerSnapExHelper, val listener: OnListener? = null) {
    interface OnListener {
        fun onSnapped(position: Int)
        fun onUnsnapped(position: Int)
    }

    interface PagerSnapWithTabLayoutHelperPageTitle {
        fun getPageTitle(position: Int): String?
    }

    init {
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position != this@PagerSnapWithTabLayoutHelper.snapHelper.getCurrentSnappedPosition()) {
                    this@PagerSnapWithTabLayoutHelper.snapHelper.scrollToSnapPosition(tab.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        snapHelper.setOnSnappedListener(object : PagerSnapExHelper.OnSnappedListener {
            override fun onSnapped(position: Int, itemCount: Int) {
                val tab: TabLayout.Tab? = this@PagerSnapWithTabLayoutHelper.tabLayout.getTabAt(position)
                if (tab != null && !tab.isSelected) {
                    tab.select()
                }
                listener?.onSnapped(position)
            }

            override fun onUnsnapped(position: Int, itemCount: Int) {
                listener?.onUnsnapped(position)
            }
        })
        updateTabs(snapHelper.getCurrentSnappedPosition())
    }

    fun updateTabs(selectedPosition: Int) {
        val recyclerView = snapHelper.getRecyclerView() ?: return
        val adapter = recyclerView.adapter
        tabLayout.removeAllTabs()
        if (adapter is PagerSnapWithTabLayoutHelperPageTitle) {
            for (index in 0 until adapter.itemCount) {
                tabLayout.addTab(tabLayout.newTab().setText(adapter.getPageTitle(index)), selectedPosition == index)
            }

            val currentTab = tabLayout.getTabAt(selectedPosition)
            if (currentTab != null) {
                tabLayout.post { currentTab.select() }
            }
        }
    }
}
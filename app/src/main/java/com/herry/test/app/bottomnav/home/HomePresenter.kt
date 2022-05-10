package com.herry.test.app.bottomnav.home

import com.herry.test.R
import com.herry.test.app.bottomnav.home.form.HomeBottomNavControlForm
import com.herry.test.app.bottomnav.home.form.HomeBottomNavControlItemForm
import com.herry.test.app.bottomnav.home.form.HomeBottomNavScreenId

class HomePresenter : HomeContract.Presenter() {
    private var currentScreen: HomeBottomNavScreenId? = null
    private val tabs = arrayListOf(
        // mix
        HomeBottomNavControlItemForm.Model(id = R.id.bottom_nav_new_navigation, icon = R.drawable.ic_navigation_new, "New"),
        // mix
        HomeBottomNavControlItemForm.Model(id = R.id.bottom_nav_feeds_navigation, icon = R.drawable.ic_navigation_feeds, "Feeds"),
        // mix
        HomeBottomNavControlItemForm.Model(id = R.id.bottom_nav_create_navigation, icon = R.drawable.ic_navigation_create, "Create"),
        // mix
        HomeBottomNavControlItemForm.Model(id = R.id.bottom_nav_me_navigation, icon = R.drawable.ic_navigation_me, "Me")
    )

    override fun onLaunch(view: HomeContract.View, recreated: Boolean) {
        launch {
            loadNavigator(!recreated)
        }
    }

    private fun loadNavigator(init: Boolean) {
        displayNavigator(false, init)
    }

    private fun displayNavigator(hasNewNotice: Boolean, isStart: Boolean) {
        view?.getContext() ?: return

        val currentScreen = this.currentScreen ?: HomeBottomNavScreenId.CREATE

        setCurrent(currentScreen, isStart = isStart, force = true)
    }

    override fun setCurrent(id: HomeBottomNavScreenId, isStart: Boolean, force: Boolean) {
        if (this.currentScreen == id && !force) {
            return
        }

        this.currentScreen = id

        view?.onSelectTab(
            model = HomeBottomNavControlForm.Model(
                selected = id,
                items = tabs
            ),
            isStart = isStart
        )
    }

    override fun getCurrent(): HomeBottomNavScreenId? = this.currentScreen
}
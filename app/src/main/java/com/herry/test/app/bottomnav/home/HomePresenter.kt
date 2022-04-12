package com.herry.test.app.bottomnav.home

import com.herry.test.app.bottomnav.form.HomeBottomNavigatorForm

class HomePresenter : HomeContract.Presenter() {
    private var currentScreen: HomeBottomNavigatorForm.ItemType? = null

    override fun onLaunch(view: HomeContract.View, recreated: Boolean) {
        loadNavigator()
    }

    private fun loadNavigator() {
        displayNavigator(false)
    }

    private fun displayNavigator(hasNewNotice: Boolean) {
        view?.getContext() ?: return

        val currentScreen = this.currentScreen ?: HomeBottomNavigatorForm.ItemType.FEATURE

        view?.onNavigator(
            HomeBottomNavigatorForm.Model(
                hasNew = hasNewNotice,
                selected = currentScreen
            )
        )
    }

    override fun setCurrent(type: HomeBottomNavigatorForm.ItemType) {
        this.currentScreen = type
    }

    override fun getCurrent(): HomeBottomNavigatorForm.ItemType? = this.currentScreen
}
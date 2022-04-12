package com.herry.test.app.bottomnav.home

import com.herry.libs.mvp.MVPView
import com.herry.test.app.base.nav.BaseNavPresenter
import com.herry.test.app.bottomnav.form.HomeBottomNavigatorForm

interface HomeContract {
    interface View : MVPView<Presenter> {
        fun onNavigator(model: HomeBottomNavigatorForm.Model)
    }

    abstract class Presenter: BaseNavPresenter<View>() {
        abstract fun setCurrent(type: HomeBottomNavigatorForm.ItemType)
        abstract fun getCurrent(): HomeBottomNavigatorForm.ItemType?
    }
}
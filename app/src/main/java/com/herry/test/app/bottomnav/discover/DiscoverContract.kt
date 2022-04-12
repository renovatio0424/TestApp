package com.herry.test.app.bottomnav.discover

import com.herry.libs.mvp.MVPView
import com.herry.test.app.base.nav.BaseNavPresenter

interface DiscoverContract {
    interface View : MVPView<Presenter> {
    }

    abstract class Presenter: BaseNavPresenter<View>() {
    }
}
package com.herry.test.app.bottomnav.me

import com.herry.libs.mvp.MVPView
import com.herry.test.app.base.nav.BaseNavPresenter

interface MeContract {
    interface View : MVPView<Presenter> {
    }

    abstract class Presenter: BaseNavPresenter<View>() {
    }
}
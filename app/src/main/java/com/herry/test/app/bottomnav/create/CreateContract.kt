package com.herry.test.app.bottomnav.create

import com.herry.libs.mvp.MVPView
import com.herry.test.app.base.nav.BaseNavPresenter

interface CreateContract {
    interface View : MVPView<Presenter> {
    }

    abstract class Presenter: BaseNavPresenter<View>() {
    }
}
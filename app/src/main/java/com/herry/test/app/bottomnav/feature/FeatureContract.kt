package com.herry.test.app.bottomnav.feature

import com.herry.libs.mvp.MVPView
import com.herry.test.app.base.nav.BaseNavPresenter

interface FeatureContract {
    interface View : MVPView<Presenter> {
    }

    abstract class Presenter: BaseNavPresenter<View>() {
    }
}
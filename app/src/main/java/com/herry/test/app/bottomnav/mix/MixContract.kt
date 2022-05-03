package com.herry.test.app.bottomnav.mix

import com.herry.libs.mvp.MVPView
import com.herry.test.app.base.nav.BaseNavPresenter

interface MixContract {
    interface View : MVPView<Presenter> {
        fun onUpdateCounts(counts: Int)
    }

    abstract class Presenter: BaseNavPresenter<View>() {
    }
}
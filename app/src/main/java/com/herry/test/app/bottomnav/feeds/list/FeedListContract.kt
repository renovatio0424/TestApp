package com.herry.test.app.bottomnav.feeds.list

import com.herry.libs.mvp.MVPView
import com.herry.test.app.base.nav.BaseNavPresenter

interface FeedListContract {
    interface View : MVPView<Presenter> {
    }

    abstract class Presenter: BaseNavPresenter<View>() {
    }
}
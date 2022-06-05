package com.herry.test.app.sample.feeds.search

import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.model.NodeRoot
import com.herry.test.app.base.nav.BaseNavPresenter

interface SearchFeedsContract {
    interface View : MVPView<Presenter> {
        val recentlyRoot: NodeRoot
        val searchRoot: NodeRoot

        fun onChangedViewMode(mode: ViewMode)
    }

    abstract class Presenter: BaseNavPresenter<View>() {
        abstract fun searchFeed(keyword: String)
    }

    enum class ViewMode {
        RECENTLY,
        SEARCH
    }

    class EmptyModel
}
package com.herry.test.app.bottomnav.feeds.list

import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.nav.BaseNavPresenter
import com.herry.test.repository.feed.db.Feed
import com.herry.test.widget.SingleLineChipsForm

interface FeedsListContract {
    interface View : MVPView<Presenter>, INodeRoot {
        fun onUpdateCategories(categories: SingleLineChipsForm.Chips, current: Int)
        fun onLaunched(counts: Int)
        fun onScrollToPosition(position: Int)
    }

    abstract class Presenter: BaseNavPresenter<View>() {
        abstract fun loadMore()
        abstract fun setCurrentPosition(position: Int)
    }

    enum class Categories(val id: Int, val title: String) {
        ALL(0, "All"),
        MARKETING(1, "Marketing"),
        CORPORATE(2, "Corporate"),
        EDUCATION(3, "Education"),
        CELEBRATIONS(4, "Celebrations"),
        FESTIVAL(5, "Festival"),
        SOCIAL_MEDIA(6, "Social media"),
        VLOG(7, "Vlog"),
        REVIEW(8, "Review"),
        MEMES(9, "Memes"),
        INTRO(10, "Intro");
    }
}
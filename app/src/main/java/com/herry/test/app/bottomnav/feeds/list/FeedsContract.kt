package com.herry.test.app.bottomnav.feeds.list

import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.model.NodeRoot
import com.herry.test.app.base.nav.BaseNavPresenter

interface FeedsContract {
    interface View : MVPView<Presenter> {
        val categoryFeedsRoot: NodeRoot
        fun onScrollToCategory(position: Int)
    }

    abstract class Presenter: BaseNavPresenter<View>() {
        abstract fun setCurrentCategory(position: Int)
        abstract fun getCategoryName(position: Int): String
    }

    @Suppress("unused")
    enum class FeedCategory(val id: Int, val title: String) {
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

        companion object {
            fun generate(id: Int): FeedCategory? = values().firstOrNull { it.id == id }
        }
    }
}
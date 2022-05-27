package com.herry.test.app.sample.hots

import com.google.android.exoplayer2.ExoPlayer
import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.nav.BaseNavPresenter
import com.herry.test.app.sample.hots.forms.FeedForm

interface NewContract {
    interface View : MVPView<Presenter>, INodeRoot {
        fun onLaunched(count: Int)
        fun onScrollTo(position: Int)
    }

    abstract class Presenter: BaseNavPresenter<View>() {
        abstract fun setCurrentPosition(position: Int)
        abstract fun preparePlayer(model: FeedForm.Model?): ExoPlayer?
        abstract fun play(position: Int)
        abstract fun stop(position: Int)
        abstract fun stop(model: FeedForm.Model?)
        abstract fun togglePlay(model: FeedForm.Model?)
        abstract fun toggleVolume(model: FeedForm.Model?)
        abstract fun loadMore()
    }
}
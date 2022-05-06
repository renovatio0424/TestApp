package com.herry.test.app.bottomnav.mix

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.nav.BaseNavPresenter
import com.herry.test.app.bottomnav.mix.forms.FeedDetailForm

interface MixContract {
    interface View : MVPView<Presenter>, INodeRoot {
        fun onLaunched(count: Int)
        fun onScrollTo(position: Int)
    }

    abstract class Presenter: BaseNavPresenter<View>() {
        abstract fun setCurrentPosition(position: Int)
        abstract fun preparePlayer(model: FeedDetailForm.Model?): ExoPlayer?
        abstract fun play(position: Int)
        abstract fun stop(position: Int)
        abstract fun loadMore()
    }
}
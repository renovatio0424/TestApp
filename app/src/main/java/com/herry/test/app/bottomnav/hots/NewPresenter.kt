package com.herry.test.app.bottomnav.hots

import com.google.android.exoplayer2.ExoPlayer
import com.herry.libs.media.exoplayer.ExoPlayerManager
import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodeHelper
import com.herry.libs.nodeview.model.NodeModelGroup
import com.herry.test.app.bottomnav.hots.forms.FeedForm
import com.herry.test.repository.feed.db.FeedDB
import com.herry.test.repository.feed.db.FeedDBRepository
import io.reactivex.Observable


class NewPresenter : NewContract.Presenter() {

    companion object {
        const val PAGE_SIZE = 10
    }

    private var feedDatabase: FeedDB? = null
    private var feedRepository: FeedDBRepository? = null

    private val feedNodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()
    private var currentPosition: Int = 0

    private val exoPlayerManger: ExoPlayerManager = ExoPlayerManager(
        context = {
            view?.getViewContext()
        },
        isSingleInstance = false
    )

    override fun onAttach(view: NewContract.View) {
        super.onAttach(view)

        val context = view.getViewContext() ?: return
        feedDatabase = FeedDB.getInstance(context)?.also { db ->
            feedRepository = FeedDBRepository(db.dao())
        }

        view.root.beginTransition()
        NodeHelper.addNode(view.root, feedNodes)
        view.root.endTransition()
    }

    override fun onDetach() {
        feedRepository = null
        feedDatabase = null

        super.onDetach()
    }

    override fun onLaunch(view: NewContract.View, recreated: Boolean) {
        launch {
            load(!recreated)
        }
    }

    override fun onResume(view: NewContract.View) {
        launch {
            load(false)
        }
    }

    override fun onPause(view: NewContract.View) {
        launch {
            stopPlayAll()
        }
    }

    private fun load(init: Boolean) {
        if (init) {
            loadFeeds()
        } else {
            reloadFeeds()
        }
    }

    private fun loadFeeds(page: Int = 1) {
        subscribeObservable(Observable.create<MutableList<FeedForm.Model>> { emitter ->
            val list: MutableList<FeedForm.Model> = mutableListOf()

            feedRepository?.getNewFeeds(page, PAGE_SIZE)?.forEachIndexed { index, feed ->
                list.add(FeedForm.Model(index, feed))
            }

            emitter.onNext(list)
            emitter.onComplete()
        }, { videos ->
            display(page == 1, videos)
        })
    }

    private fun reloadFeeds() {
        val videos = NodeHelper.getChildrenModels<FeedForm.Model>(feedNodes)
        if (videos.size <= 0) {
            loadFeeds(1)
        } else {
            feedNodes.beginTransition()
            feedNodes.clearChild()
            feedNodes.endTransition()
            display(false, videos)
            view?.onScrollTo(currentPosition)
        }
    }

    private fun display(reset: Boolean, list: MutableList<FeedForm.Model>) {
        this.feedNodes.beginTransition()
        if (reset) {
            val nodes = NodeHelper.createNodeGroup()
            NodeHelper.addModels(nodes, *list.toTypedArray())
            NodeHelper.upSert(this.feedNodes, nodes)
        } else {
            NodeHelper.addModels(this.feedNodes, *list.toTypedArray())
        }
        this.feedNodes.endTransition()

        if (reset) {
            view?.onLaunched(this.feedNodes.getChildCount())
        }
    }

    override fun setCurrentPosition(position: Int) {
        this.currentPosition = position
    }

    override fun preparePlayer(model: FeedForm.Model?): ExoPlayer? {
        model ?: return null
        return exoPlayerManger.prepare(model.feed.projectId, model.feed.videoPath)
    }

    private fun getFeedModelFromFeeds(position: Int): FeedForm.Model?{
        val nodePosition = feedNodes.getNodePosition(position) ?: return null
        val node = feedNodes.getNode(nodePosition) ?: return null
        return node.model as? FeedForm.Model
    }

    override fun play(position: Int) {
        val model = getFeedModelFromFeeds(position) ?: return
        exoPlayerManger.play(model.feed.projectId, model.feed.videoPath, true)
    }

    override fun stop(position: Int) {
        val model = getFeedModelFromFeeds(position) ?: return
        exoPlayerManger.stop(model.feed.projectId)
    }

    override fun stop(model: FeedForm.Model?) {
        model ?: return
        exoPlayerManger.stop(model.feed.projectId)
    }

    override fun togglePlay(model: FeedForm.Model?) {
        model ?: return

        val id = model.feed.projectId
        if (exoPlayerManger.isPlaying(id)) {
            // to pause
            exoPlayerManger.pause(id)
        } else if (exoPlayerManger.isReadyToPlay(id)){
            // to resume
            exoPlayerManger.resume(id)
        }
    }

    override fun toggleVolume(model: FeedForm.Model?) {
        if (!exoPlayerManger.isMute()) {
            exoPlayerManger.mute()
        } else {
            exoPlayerManger.unMute()
        }
    }

    private fun stopPlayAll() {
        exoPlayerManger.stopAll()
    }

    override fun loadMore() {
        val feeds = NodeHelper.getChildrenModels<FeedForm.Model>(feedNodes)
        val currentPage = feeds.size / PAGE_SIZE
        loadFeeds(if (currentPage > 0) currentPage + 1 else 1)
    }
}
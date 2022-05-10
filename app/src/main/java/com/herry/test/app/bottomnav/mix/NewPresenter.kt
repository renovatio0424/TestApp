package com.herry.test.app.bottomnav.mix

import com.google.android.exoplayer2.ExoPlayer
import com.herry.libs.media.exoplayer.ExoPlayerManager
import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodeHelper
import com.herry.libs.nodeview.model.NodeModelGroup
import com.herry.test.app.bottomnav.data.FeedsData
import com.herry.test.app.bottomnav.mix.forms.FeedForm
import io.reactivex.Observable


class NewPresenter : NewContract.Presenter() {

    private val feedNodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()
    private var currentPosition: Int = 0

    private val exoPlayerManger: ExoPlayerManager = ExoPlayerManager(
        context = {
            view?.getContext()
        },
        isSingleInstance = false
    )

    override fun onAttach(view: NewContract.View) {
        super.onAttach(view)

        view.root.beginTransition()
        NodeHelper.addNode(view.root, feedNodes)
        view.root.endTransition()
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

    private fun loadFeeds(lastId: String = "-1") {
        subscribeObservable(Observable.create<MutableList<FeedForm.Model>> { emitter ->
            val list: MutableList<FeedForm.Model> = mutableListOf()
            val feeds: LinkedHashMap<String, String> = FeedsData.getFeeds(lastId, 10)
            feeds.keys.forEach { id ->
                val url = feeds[id]
                if (url != null) {
                    list.add(FeedForm.Model(id, url))
                }
            }
            emitter.onNext(list)
            emitter.onComplete()
        }, { videos ->
            display(lastId == "-1", videos)
        })
    }

    private fun reloadFeeds() {
        val videos = NodeHelper.getChildrenModels<FeedForm.Model>(feedNodes)
        if (videos.size <= 0) {
            loadFeeds("-1")
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

        return exoPlayerManger.prepare(model.id, model.url)
    }

    private fun getFeedModelFromFeeds(position: Int): FeedForm.Model?{
        val nodePosition = feedNodes.getNodePosition(position) ?: return null
        val node = feedNodes.getNode(nodePosition) ?: return null
        return node.model as? FeedForm.Model
    }

    override fun play(position: Int) {
        val model = getFeedModelFromFeeds(position) ?: return

        exoPlayerManger.play(model.id, model.url, true)
    }

    override fun stop(position: Int) {
        val model = getFeedModelFromFeeds(position) ?: return

        exoPlayerManger.stop(model.id)
    }

    override fun stop(model: FeedForm.Model?) {
        model ?: return
        exoPlayerManger.stop(model.id)
    }

    override fun togglePlay(model: FeedForm.Model?) {
        model ?: return

        val id = model.id
        if (exoPlayerManger.isPlaying(id)) {
            // to pause
            exoPlayerManger.pause(id)
        } else if (exoPlayerManger.isReadyToPlay(id)){
            // to resume
            exoPlayerManger.resume(id)
        }
    }

    private fun stopPlayAll() {
        exoPlayerManger.stopAll()
    }

    override fun loadMore() {
        val feeds = NodeHelper.getChildrenModels<FeedForm.Model>(feedNodes)
        val lastId = if (feeds.isNotEmpty()) feeds.last().id else return
        loadFeeds(lastId)
    }
}
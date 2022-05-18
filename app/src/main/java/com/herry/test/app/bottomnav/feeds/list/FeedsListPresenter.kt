package com.herry.test.app.bottomnav.feeds.list

import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodeHelper
import com.herry.libs.nodeview.model.NodeModelGroup
import com.herry.test.app.bottomnav.hots.NewPresenter
import com.herry.test.repository.feed.db.Feed
import com.herry.test.repository.feed.db.FeedDB
import com.herry.test.repository.feed.db.FeedDBRepository
import io.reactivex.Observable

class FeedsListPresenter(private val category: FeedsListContract.Categories) : FeedsListContract.Presenter() {
    companion object {
        const val PAGE_SIZE = 30
    }

    private var feedDatabase: FeedDB? = null
    private var feedRepository: FeedDBRepository? = null

    private val feedNodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()

    private var currentPosition: Int = 0

    override fun onAttach(view: FeedsListContract.View) {
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

    override fun onLaunch(view: FeedsListContract.View, recreated: Boolean) {
        launch {
            load(!recreated)
        }
    }

    override fun onResume(view: FeedsListContract.View) {
        launch {
            load(false)
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
        subscribeObservable(Observable.create<MutableList<Feed>> { emitter ->
            val list: MutableList<Feed> = mutableListOf()

            feedRepository?.getList(category = category.id, page = page, pageSize = PAGE_SIZE)?.let { feeds ->
                list.addAll(feeds)
            }

            emitter.onNext(list)
            emitter.onComplete()
        }, { videos ->
            display(page == 1, videos)
        })
    }

    private fun reloadFeeds() {
        val videos = NodeHelper.getChildrenModels<Feed>(feedNodes)
        if (videos.size <= 0) {
            loadFeeds(1)
        } else {
            feedNodes.beginTransition()
            feedNodes.clearChild()
            feedNodes.endTransition()
            display(false, videos)
            view?.onScrollToPosition(currentPosition)
        }
    }

    private fun display(reset: Boolean, feeds: MutableList<Feed>) {
        this.feedNodes.beginTransition()
        if (reset) {
            val nodes = NodeHelper.createNodeGroup()
            NodeHelper.addModels(nodes, *feeds.toTypedArray())
            NodeHelper.upSert(this.feedNodes, nodes)
        } else {
            NodeHelper.addModels(this.feedNodes, *feeds.toTypedArray())
        }
        this.feedNodes.endTransition()

        if (reset) {
            view?.onLaunched(this.feedNodes.getChildCount())
        }
    }

//    private fun updateCategories() {
//        val categories = arrayListOf<SingleLineChipsForm.Chip>().apply {
//            FeedsListContract.Categories.values().forEach { category ->
//                this.add(SingleLineChipsForm.Chip(text = category.name))
//            }
//        }
//        view?.onUpdateCategories(
//            categories = SingleLineChipsForm.Chips(categories), 0)
//    }

    override fun setCurrentPosition(position: Int) {
        this.currentPosition = position
    }

    override fun loadMore() {
        val feeds = NodeHelper.getChildrenModels<Feed>(feedNodes)
        val currentPage = feeds.size / NewPresenter.PAGE_SIZE
        loadFeeds(if (currentPage > 0) currentPage + 1 else 1)
    }
}
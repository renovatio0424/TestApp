package com.herry.test.app.bottomnav.feeds.list

import com.herry.libs.nodeview.model.NodeHelper
import com.herry.libs.widget.view.recyclerview.tabrecycler.TabRecyclerContract
import com.herry.libs.widget.view.recyclerview.tabrecycler.TabRecyclerLoadingType
import com.herry.libs.widget.view.recyclerview.tabrecycler.TabRecyclerPresenter
import com.herry.test.repository.feed.db.Feed
import com.herry.test.repository.feed.db.FeedDB
import com.herry.test.repository.feed.db.FeedDBRepository
import com.herry.test.rx.LastOneObservable
import com.herry.test.rx.RxUtil
import io.reactivex.Observable

class FeedCategoryFeedsPresenter(val category: FeedsContract.FeedCategory) : TabRecyclerPresenter() {

    companion object {
        const val PAGE_SIZE = 30
    }

    private val lastOneObservable = LastOneObservable<Pair<Boolean, MutableList<Feed>>>(
        {
            display(it.first, it.second)
        }
    )

    private var feedDatabase: FeedDB? = null
    private var feedRepository: FeedDBRepository? = null

    private var currentPosition: Int = 0

    override fun onAttach(view: TabRecyclerContract.View) {
        super.onAttach(view)

        val context = view.getViewContext() ?: return
        feedDatabase = FeedDB.getInstance(context)?.also { db ->
            feedRepository = FeedDBRepository(db.dao())
        }
    }

    override fun onDetach() {
        lastOneObservable.dispose()

        feedRepository = null
        feedDatabase = null

        super.onDetach()
    }

    override fun onLaunch() {
//        Trace.d("Herry", "onLaunch : $category")
        loadFeeds(true)
    }

    override fun onResume() {
//        Trace.d("Herry", "onResume  $category to : $currentPosition")
        if (isEmpty()) {
            loadFeeds(true)
        } else {
            view?.onScrollToPosition(currentPosition)
        }
    }

    override fun loadMore() {
        val feeds = NodeHelper.getChildrenModels<Feed>(nodes)
        val currentPage = feeds.size / PAGE_SIZE
        loadFeeds(false, if (currentPage > 0) currentPage + 1 else 1)
    }

    override fun refresh(loading: TabRecyclerLoadingType) {
    }

    override fun scrollToPosition(position: Int) {
        this.currentPosition = position
//        Trace.d("Herry", "scrollToPosition  $category to : $currentPosition")
    }

    override fun onPause() {
    }

    override fun relaunched(recreated: Boolean) {
    }

    private fun loadFeeds(init: Boolean, page: Int = 1) {
        if (init) {
            lastOneObservable.dispose()
        }

        if (lastOneObservable.isDisposed()) {
            if (init) {
                view?.onLoadView(true)
            }
            lastOneObservable.subscribe(
                RxUtil.setPresenterObservable(
                    observable = Observable.create<MutableList<Feed>> { emitter ->
                        val list: MutableList<Feed> = mutableListOf()

                        feedRepository?.getList(category = category.id, page = page, pageSize = PAGE_SIZE)?.let { feeds ->
                            list.addAll(feeds)
                        }

                        emitter.onNext(list)
                        emitter.onComplete()
                    }, //.delay((if (init) 500 else 0).toLong(), TimeUnit.MILLISECONDS),
                    view = this::view,
                    loadView = false
                )
                    .map {
                        if (init) {
                            view?.onLoadView(false)
                        }
                        Pair(init, it)
                    }
            )
        }
    }

    private fun display(init: Boolean, list: MutableList<Feed>) {
        if (init) {
            // sets results
            this.nodes.beginTransition()

            val nodes = NodeHelper.createNodeGroup()
            NodeHelper.addModels(nodes, *list.toTypedArray())
            NodeHelper.upSert(this.nodes, nodes)

            this.nodes.endTransition()

            view?.onEmptyView(list.isEmpty())
        } else if(list.isNotEmpty()){
            this.nodes.beginTransition()
            NodeHelper.addModels(nodes, *list.toTypedArray())
            this.nodes.endTransition()
        }
    }
}
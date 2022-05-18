package com.herry.test.app.bottomnav.feeds.list

import android.util.Log
import com.herry.test.repository.feed.db.Feed
import com.herry.test.repository.feed.db.FeedDB
import com.herry.test.repository.feed.db.FeedDBRepository
import com.herry.test.widget.SingleLineChipsForm
import io.reactivex.Observable

class FeedsListPresenter : FeedsListContract.Presenter() {
    private var feedDatabase: FeedDB? = null
    private var feedRepository: FeedDBRepository? = null

    override fun onAttach(view: FeedsListContract.View) {
        super.onAttach(view)

        val context = view.getViewContext() ?: return
        feedDatabase = FeedDB.getInstance(context)?.also { db ->
            feedRepository = FeedDBRepository(db.dao())
        }
    }

    override fun onDetach() {
        feedRepository = null
        feedDatabase = null

        super.onDetach()
    }

    override fun onLaunch(view: FeedsListContract.View, recreated: Boolean) {
        launch {
            load()
        }
    }

    private fun loadFeeds(): Observable<MutableList<Feed>> {
        return Observable.create { emitter ->
            val list: MutableList<Feed> = mutableListOf()

            feedRepository?.getList(category = 1)?.let { feeds ->
                list.addAll(feeds)
            }
            emitter.onNext(list)
            emitter.onComplete()
        }
    }

    private fun load() {
        subscribeObservable(
            loadFeeds(),
            onNext = { feeds ->
                display(feeds)
            }
        )
    }

    private fun display(feeds: MutableList<Feed>) {
        Log.d("Herry", "feeds counts = ${feeds.size}")
    }

    private fun updateCategories() {
        val categories = arrayListOf<SingleLineChipsForm.Chip>().apply {
            FeedsListContract.Categories.values().forEach { category ->
                this.add(SingleLineChipsForm.Chip(text = category.name))
            }
        }
        view?.onUpdateCategories(
            categories = SingleLineChipsForm.Chips(categories), 0)
    }
}
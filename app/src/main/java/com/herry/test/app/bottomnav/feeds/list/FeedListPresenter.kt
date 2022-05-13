package com.herry.test.app.bottomnav.feeds.list

import android.content.Context
import android.util.Log
import androidx.lifecycle.coroutineScope
import com.herry.test.repository.feed.db.Feed
import com.herry.test.repository.feed.db.FeedDB
import com.herry.test.repository.feed.db.FeedDBRepository
import io.reactivex.Observable

class FeedListPresenter : FeedListContract.Presenter() {
    private var feedDatabase: FeedDB? = null
    private var feedRepository: FeedDBRepository? = null

    override fun onAttach(view: FeedListContract.View) {
        super.onAttach(view)

        val context = view.getContext() ?: return
        val lifecycleScope = lifecycleOwner?.lifecycle?.coroutineScope ?: return
        feedDatabase = FeedDB.getInstance(context, lifecycleScope)?.also { db ->
            feedRepository = FeedDBRepository(db.dao())
        }
    }

    override fun onDetach() {
        feedRepository = null
        feedDatabase = null

        super.onDetach()
    }

    override fun onLaunch(view: FeedListContract.View, recreated: Boolean) {
        launch {
            load()
        }
    }

    private fun loadDefaultFeedDatabase(context: Context): Observable<Boolean> {
        return Observable.create { emitter ->
            feedRepository?.setDefaultRecords(context)
            emitter.onNext(true)
            emitter.onComplete()
        }
    }

    private fun loadFeeds(): Observable<MutableList<Feed>> {
        return Observable.create { emitter ->
            val list: MutableList<Feed> = mutableListOf()

            feedRepository?.getList()?.let { feeds ->
                list.addAll(feeds)
            }
            emitter.onNext(list)
            emitter.onComplete()
        }
    }

    private fun load() {
        val context = view?.getContext() ?: return

        subscribeObservable(
            loadDefaultFeedDatabase(context).flatMap {
                loadFeeds()
            },
            onNext = { feeds ->
                display(feeds)
            }
        )
    }

    private fun display(feeds: MutableList<Feed>) {
        Log.d("Herry", "feeds counts = ${feeds.size}")
    }
}
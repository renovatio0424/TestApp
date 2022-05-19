package com.herry.test.repository.feed.db

import androidx.annotation.WorkerThread

@Suppress("unused")
class FeedDBRepository(private val dao: FeedDao) {

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    fun getList(category: Int = 0, page: Int = 1, pageSize: Int = 0): List<Feed> {
        return dao.getList(category, page - 1, pageSize)
    }

    @WorkerThread
    fun getNewFeeds(page: Int = 1, pageSize: Int = 10): List<Feed> {
        return dao.getNewList(page - 1, pageSize)
    }
}
package com.herry.test.repository.feed.db

import androidx.annotation.WorkerThread

@Suppress("unused")
class FeedDBRepository(private val dao: FeedDao) {

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    fun getList(category: Int = 0, lastProjectId: String = "", pageSize: Int = 30): List<Feed> {
        return dao.getList(category, lastProjectId, pageSize)
    }

    @WorkerThread
    fun getList(projectIds: List<String>): List<Feed> {
        if (projectIds.isEmpty()) {
            return listOf()
        }

        return dao.getList(projectIds)
    }

    @WorkerThread
    fun getNewFeeds(lastProjectId: String = "", pageSize: Int = 10): List<Feed> {
        return dao.getNewFeeds(lastProjectId, pageSize)
    }

    @WorkerThread
    fun getTagFeeds(tag: String, lastProjectId: String = "", pageSize: Int = 10): List<Feed> {
        if (tag.isBlank()) {
            return listOf()
        }

        return dao.getTagFeeds("%$tag%", lastProjectId, pageSize)
    }
}
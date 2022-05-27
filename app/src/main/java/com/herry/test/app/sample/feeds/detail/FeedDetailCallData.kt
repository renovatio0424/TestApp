package com.herry.test.app.sample.feeds.detail

import com.herry.test.app.sample.data.FeedCategory
import com.herry.test.repository.feed.db.Feed

data class FeedDetailCallData(
    val projects: MutableList<String> = mutableListOf(),
    val mode: FeedDetailListMode,
    val feedsCategory: FeedCategory = FeedCategory.ALL,
    val selectedFeed: Feed
): java.io.Serializable

enum class FeedDetailListMode {
    SINGLE,
    NEWS,
    FEEDS,
    HASHES
}
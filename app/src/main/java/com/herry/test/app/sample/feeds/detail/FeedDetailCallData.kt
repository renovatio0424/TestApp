package com.herry.test.app.sample.feeds.detail

import com.herry.test.app.sample.data.FeedCategory
import com.herry.test.repository.feed.db.Feed

open class FeedDetailCallData(
    val loadedProjectCounts: Int = 0,
    val selectedFeed: Feed,
    val mode: FeedDetailListMode
): java.io.Serializable

class CategoryFeedsDetailCallData(
    loadedProjectCounts: Int = 0,
    selectedFeed: Feed,
    val feedsCategory: FeedCategory = FeedCategory.ALL
) : FeedDetailCallData(loadedProjectCounts = loadedProjectCounts, selectedFeed = selectedFeed, mode = FeedDetailListMode.FEEDS)

class TagFeedsDetailCallData(
    loadedProjectCounts: Int = 0,
    selectedFeed: Feed,
    val tag: String
) : FeedDetailCallData(loadedProjectCounts = loadedProjectCounts, selectedFeed = selectedFeed, mode = FeedDetailListMode.TAGS)

enum class FeedDetailListMode {
    SINGLE,
    TAGS,
    FEEDS,
    HASHES
}
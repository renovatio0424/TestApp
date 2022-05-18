package com.herry.test.repository.feed.db

import android.content.Context
import android.util.JsonReader
import androidx.annotation.WorkerThread
import java.io.IOException
import java.io.InputStreamReader

@Suppress("unused")
class FeedDBRepository(private val dao: FeedDao) {

    @WorkerThread
    fun getList(category: Int = 0, page: Int = 1, pageSize: Int = 0): List<Feed> {
        return dao.getList(category, page - 1, pageSize)
    }

    @WorkerThread
    fun getNewFeeds(page: Int = 1, pageSize: Int = 10): List<Feed> {
        return dao.getNewList(page - 1, pageSize)
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(feed: Feed) {
        dao.insert(feed)
    }

    private fun getFeedsFromPreloaded(context: Context): List<Feed> {
        val assetManager = context.assets ?: return listOf()
        assetManager.open("feeds.json").use { inputStream ->
            JsonReader(InputStreamReader(inputStream, "UTF-8")).use { reader ->
                val list = mutableListOf<Feed>()
                reader.beginArray()
                while (reader.hasNext()) {
                    val feed = readFeed(reader)
                    if (feed != null) {
                        list.add(feed)
                    }
                }
                reader.endArray()
                return list
            }
        }
    }

    @Throws(IOException::class)
    private fun readFeed(reader: JsonReader): Feed? {
        var projectId: String? = null
        var imagePath: String? = null
        var videoPath: String? = null
        var width: Int? = null
        var height: Int? = null
        var category: Int? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "projectId" -> {
                    projectId = reader.nextString()
                }
                "imagePath" -> {
                    imagePath = reader.nextString()
                }
                "videoPath" -> {
                    videoPath = reader.nextString()
                }
                "width" -> {
                    width = reader.nextInt()
                }
                "height" -> {
                    height = reader.nextInt()
                }
                "category" -> {
                    category = reader.nextInt()
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return if (projectId?.isNotBlank() == true) {
            Feed(
                projectId = projectId,
                imagePath = imagePath ?: "",
                videoPath = videoPath ?: "",
                width = width ?: 0,
                height = height ?: 0,
                category = category ?: 1
            )
        } else {
            null
        }
    }
}
package com.herry.test.repository.feed.db

import android.content.Context
import android.util.JsonReader
import androidx.annotation.WorkerThread
import com.herry.libs.util.preferences.PreferenceHelper
import com.herry.test.sharedpref.SharedPrefKeys
import java.io.IOException
import java.io.InputStreamReader

class FeedDBRepository(private val dao: FeedDao) {

    @WorkerThread
    fun getList(): List<Feed> = dao.getList()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(feed: Feed) {
        dao.insert(feed)
    }

    @WorkerThread
    fun setDefaultRecords(context: Context) {
        if (PreferenceHelper.get(SharedPrefKeys.SET_FEED_RECORDS) == false) {
            // copy from default db file
            val list = getFeedsFromPreloaded(context)
            dao.insertAll(list)

            PreferenceHelper.set(SharedPrefKeys.SET_FEED_RECORDS, true)
        }
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
        var duration: String? = null

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
                "duration" -> {
                    duration = reader.nextString()
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
                duration = duration ?: ""
            )
        } else {
            null
        }
    }
}
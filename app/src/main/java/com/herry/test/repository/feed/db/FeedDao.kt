package com.herry.test.repository.feed.db

import androidx.room.*

@Dao
interface FeedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(feed: Feed)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(feeds: List<Feed>)

    @Update
    fun update(feed: Feed)

    @Query("DELETE from feed")
    fun deleteAll()

    @Query("SELECT * FROM feed ORDER BY update_time DESC")
    fun getList(): List<Feed>
}
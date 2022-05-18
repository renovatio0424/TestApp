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

    @Query(
        "SELECT * " +
                "FROM feed " +
                "WHERE CASE WHEN :category > 0 THEN category = :category ELSE category END " +
                "ORDER BY update_time DESC " +
                "LIMIT CASE WHEN :loadSize > 0 THEN :loadSize ELSE -1 END " +
                "OFFSET CASE WHEN :loadSize > 0 THEN :offset * :loadSize ELSE 0 END"
    )
    fun getList(category: Int, offset: Int, loadSize: Int): List<Feed>

    @Query(
        "SELECT * " +
                "FROM (SELECT * FROM feed WHERE category ORDER BY update_time DESC LIMIT 30) " +
                "LIMIT CASE WHEN :loadSize > 0 THEN :loadSize ELSE -1 END " +
                "OFFSET CASE WHEN :loadSize > 0 THEN :offset * :loadSize ELSE 0 END"
    )
    fun getNewList(offset: Int, loadSize: Int): List<Feed>
}
package com.herry.test.app.sample.repository.database.searchlog

import androidx.room.*

@Dao
interface RecentlySearchKeywordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(keyword: RecentlySearchKeyword)

    @Update
    fun update(keyword: RecentlySearchKeyword)

    @Delete
    fun delete(keyword: RecentlySearchKeyword)

    @Query(
        "SELECT * " +
                "FROM search_keyword " +
                "ORDER BY update_time DESC"
    )
    fun getList(): List<RecentlySearchKeyword>
}
package com.herry.test.app.sample.repository.database.searchlog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "search_keyword")
data class RecentlySearchKeyword(
    @PrimaryKey @ColumnInfo(name = "keyword") val keyword: String,
    @ColumnInfo(name = "update_time") val updateTime: Date
) : Serializable
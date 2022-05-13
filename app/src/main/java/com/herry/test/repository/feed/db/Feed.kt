package com.herry.test.repository.feed.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed")
data class Feed(
    @PrimaryKey @ColumnInfo(name = "project_id") val projectId: String,
    @ColumnInfo(name = "image_path") val imagePath: String,
    @ColumnInfo(name = "video_path") val videoPath: String,
    @ColumnInfo(name = "width") val width: Int,
    @ColumnInfo(name = "height") val height: Int,
    @ColumnInfo(name = "duration") val duration: String,
    @ColumnInfo(name = "create_time") val createTime: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "update_time") var updateTime: Long = System.currentTimeMillis()
)
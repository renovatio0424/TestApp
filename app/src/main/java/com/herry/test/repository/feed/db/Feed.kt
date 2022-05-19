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
    /*
     marketing	1
     corporate	2
    education	3
    celebrations	4
    festival	5
    social media	6
    vlog	7
    review	8
    memes	9
    intro	10
     */
    @ColumnInfo(name = "category") val category: Int,
    @ColumnInfo(name = "published_at") val publishedAt: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "creator_id") val creatorId: String
)
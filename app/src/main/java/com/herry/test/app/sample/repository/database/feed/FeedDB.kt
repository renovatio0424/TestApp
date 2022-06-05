package com.herry.test.app.sample.repository.database.feed

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Feed::class], version = 1)
abstract class FeedDB : RoomDatabase() {
    abstract fun dao(): FeedDao

    companion object {
        private var INSTANCE: FeedDB? = null
        private const val db = "feed.db"

        fun getInstance(context: Context): FeedDB? {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    /*context = */  context.applicationContext,
                    /*klass = */      FeedDB::class.java,
                    /*name = */     db
                )
                    .createFromAsset("feeds.db")
                    .build()
                return INSTANCE
            }
        }
    }
}
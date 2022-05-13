package com.herry.test.repository.feed.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope


@Database(entities = [Feed::class], version = 1)
abstract class FeedDB: RoomDatabase() {
    abstract fun dao(): FeedDao

    companion object {
        private var INSTANCE: FeedDB? = null
        private const val db = "feed.db"

        fun getInstance(context: Context, scope: CoroutineScope): FeedDB? {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    /*context = */  context.applicationContext,
                    /*klass = */      FeedDB::class.java,
                    /*name = */     db
                )
                    .build()
                return INSTANCE
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
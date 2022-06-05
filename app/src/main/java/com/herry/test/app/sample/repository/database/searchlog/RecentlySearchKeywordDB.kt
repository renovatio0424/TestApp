package com.herry.test.app.sample.repository.database.searchlog

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.herry.test.app.sample.repository.database.converter.DateTypeConverter

@Database(entities = [RecentlySearchKeyword::class], version = 1)
@TypeConverters(DateTypeConverter::class)
abstract class RecentlySearchKeywordDB : RoomDatabase() {
    abstract fun dao(): RecentlySearchKeywordDao

    companion object {
        private var INSTANCE: RecentlySearchKeywordDB? = null
        private const val db = "search_keyword.db"

        fun getInstance(context: Context): RecentlySearchKeywordDB? {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    /*context = */  context.applicationContext,
                    /*klass = */      RecentlySearchKeywordDB::class.java,
                    /*name = */     db
                )
                    .build()
                return INSTANCE
            }
        }
    }
}
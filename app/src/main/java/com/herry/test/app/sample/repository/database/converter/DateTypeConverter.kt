package com.herry.test.app.sample.repository.database.converter

import androidx.room.TypeConverter
import java.util.*

object DateTypeConverter {
    @TypeConverter
    fun toDate(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun toLong(value: Date): Long {
        return value.time
    }
}
package com.herry.test.app.sample.repository.database.searchlog

import androidx.annotation.WorkerThread
import com.herry.test.app.sample.repository.database.converter.DateTypeConverter

@Suppress("unused")
class RecentlySearchKeywordDBRepository(private val dao: RecentlySearchKeywordDao) {

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    fun getList(): List<RecentlySearchKeyword> {
        return dao.getList()
    }

    fun add(keyword: String) {
        dao.insert(RecentlySearchKeyword(
            keyword = keyword,
            updateTime = DateTypeConverter.toDate(System.currentTimeMillis())))
    }
}
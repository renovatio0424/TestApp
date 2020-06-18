package com.herry.test.rx

import android.database.Cursor

/**
 * Created by herry.park on 2020/06/18.
 **/
class RxCursorIterable(private val cursor: Cursor) : Iterable<Cursor> {

    companion object {
        fun from(cursor: Cursor): RxCursorIterable {
            return RxCursorIterable(cursor)
        }
    }

    override fun iterator(): Iterator<Cursor> {
        return RxCursorIterator.from(cursor)
    }

    internal class RxCursorIterator(private val cursor: Cursor) : Iterator<Cursor> {

        override fun hasNext(): Boolean {
            return !cursor.isClosed && cursor.moveToNext()
        }

        override fun next(): Cursor {
            return cursor
        }

        companion object {

            fun from(cursor: Cursor): Iterator<Cursor> {
                return RxCursorIterator(cursor)
            }
        }
    }
}
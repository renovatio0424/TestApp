package com.herry.libs.text

import android.text.Spannable
import android.text.style.CharacterStyle
import android.util.Pair
import java.util.*

/**
 * Created by herry.park
 */
class StringSpanAttribute(val string: String) {
    val spans: MutableList<Pair<CharacterStyle, Int>> = LinkedList()

    fun add(span: CharacterStyle?, flag: Int = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) {
        if (null == span) {
            return
        }

        spans.add(Pair(span, flag))
    }
}
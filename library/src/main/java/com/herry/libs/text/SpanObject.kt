package com.herry.libs.text

import android.graphics.drawable.Drawable
import android.text.style.*

/**
 * Created by herry.park
 */
class SpanObject(private val type: SpanObjectType, private val value: Any) {
    val span: Any?
        get() {
            when {
                SpanObjectType.FOREGROUND_COLOR == type -> {
                    return ForegroundColorSpan((value as Int))
                }
                SpanObjectType.BACKGROUND_COLOR == type -> {
                    return BackgroundColorSpan((value as Int))
                }
                SpanObjectType.ABSOLUTE_SIZE == type -> {
                    return AbsoluteSizeSpan((value as Int))
                }
                SpanObjectType.STYLE == type -> {
                    return StyleSpan((value as Int))
                }
                SpanObjectType.IMAGE == type -> {
                    return ImageSpan((value as Drawable), ImageSpan.ALIGN_BASELINE)
                }
                else -> return null
            }
        }
}
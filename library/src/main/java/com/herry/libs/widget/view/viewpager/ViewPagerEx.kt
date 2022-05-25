package com.herry.libs.widget.view.viewpager

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

@Suppress("MemberVisibilityCanBePrivate", "unused")
class ViewPagerEx : ViewPager {
    enum class AllowPagingDirection {
        NONE, ONLY_TO_START, ONLY_TO_END, ALL
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var pagingDirection = AllowPagingDirection.ALL

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return isPagingEnabled(event) && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return isPagingEnabled(event) && super.onInterceptTouchEvent(event)
    }

    private var initialXValue = 0f

    private fun isPagingEnabled(event: MotionEvent): Boolean {
        // enable all paging by touch movie action
        if (AllowPagingDirection.ALL == pagingDirection) {
            return true
        }

        // disable all paging by touch movie action
        if (AllowPagingDirection.NONE == pagingDirection) {
            return false
        }

        if (event.action == MotionEvent.ACTION_DOWN) {
            initialXValue = event.x
            return true
        }

        if (event.action == MotionEvent.ACTION_MOVE) {
            try {
                val diffX = event.x - initialXValue
                if (diffX > 0 && AllowPagingDirection.ONLY_TO_END == pagingDirection) {
                    // swipe from left to right detected
                    return false
                } else if (diffX < 0 && AllowPagingDirection.ONLY_TO_START == pagingDirection) {
                    // swipe from right to left detected
                    return false
                }
            } catch (ignore: Exception) {
            }
        }
        return true
    }

    fun setPagingEnabled(enabled: Boolean) {
        setAllowPagingDirection(if (enabled) AllowPagingDirection.ALL else AllowPagingDirection.NONE)
    }

    fun setAllowPagingDirection(allow: AllowPagingDirection) {
        pagingDirection = allow
    }
}

package com.herry.libs.widget.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Created by herry.park
 */
@Suppress("unused")
open class FrameLayoutEx : FrameLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var maxHeight = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var measureHeightSpec = heightMeasureSpec
        val measuredHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (maxHeight in 1 until measuredHeight) {
            val measureMode = MeasureSpec.getMode(heightMeasureSpec)
            measureHeightSpec = MeasureSpec.makeMeasureSpec(maxHeight, measureMode)
        }
        super.onMeasure(widthMeasureSpec, measureHeightSpec)
    }

    /**
     * Sets the maximum height of the view. It is not guaranteed the view will
     * be able to achieve this maximum height (for example, if its parent layout
     * constrains it with less available height).
     */
    fun setMaximumHeight(height: Int) {
        this.maxHeight = height
        requestLayout()
    }

    /**
     * Returns the maximum height of the view.
     *
     * @return the maximum height the view will try to be.
     */
    fun getMaximumHeight(): Int = maxHeight
}

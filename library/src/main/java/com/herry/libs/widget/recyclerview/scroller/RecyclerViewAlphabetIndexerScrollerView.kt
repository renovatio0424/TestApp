package com.herry.libs.widget.recyclerview.scroller

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.SectionIndexer
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.herry.libs.R


@Suppress("MemberVisibilityCanBePrivate", "unused")
class RecyclerViewAlphabetIndexerScrollerView : FrameLayout {

    private var savedState: SavedState? = null

    private var recyclerView: RecyclerView? = null

    private var adapter: RecyclerView.Adapter<*>? = null
    private val adapterObserver: RecyclerView.AdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            updateSections()
        }
    }

    private var sectionIndexer: SectionIndexer? = null
    private var sections: Array<String>? = null
    private var currentSectionIndex: Int = RecyclerView.NO_POSITION

    private var sectionBarRect: RectF = RectF()
    private var sectionBarBackgroundDrawable: Drawable? = null
    private var sectionBarWidth: Int = 0
    private var sectionTextSize = 0
    private var sectionTextTypeface = Typeface.DEFAULT
    @ColorInt
    private var sectionTextColor = Color.WHITE

    private var isHighlightEnable = false
    private var sectionHighlightTextSize = 0
    private var sectionHighlightTextTypeface = Typeface.DEFAULT_BOLD
    @ColorInt
    private var sectionHighlightTextColor = Color.WHITE

    private var isSectionPreviewEnable = true
    private var sectionPreviewViewWidth: Int = 0
    private var sectionPreviewViewHeight: Int = 0
    private var sectionPreviewBackgroundDrawable: Drawable? = null
    private var sectionPreviewTextTypeface = Typeface.DEFAULT
    private var sectionPreviewTextSize = 0
    @ColorInt
    private var sectionPreviewTextColor = Color.WHITE

    private var sectionPreviewHideDelay = 0
    private val sectionPreviewHideRunnable = Runnable {
        invalidate()
    }

    private val sectionTextPaint = Paint()
    private val sectionPreviewTextPaint = Paint()

    private var isIndexing = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        val attr = context.obtainStyledAttributes(attrs, R.styleable.RecyclerViewAlphabetIndexerScrollerView)

        sectionBarBackgroundDrawable = attr.getDrawable(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionBarBackground)
        if (sectionBarBackgroundDrawable == null) {
            sectionBarBackgroundDrawable = ColorDrawable(Color.argb(0x80, 0, 0, 0))
        }
        sectionBarWidth = attr.getDimensionPixelSize(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionBarWidth, convertDpToPx(context, 30f))

        val sectionTextStyle = attr.getInt(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionTextStyle, 0)
        sectionTextTypeface = convertAttrTextStyleToTypeface(sectionTextStyle)
        sectionTextColor = attr.getColor(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionTextColor, Color.WHITE)
        sectionTextSize = attr.getDimensionPixelSize(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionTextSize, convertDpToPx(context, 10f))

        sectionHighlightTextSize = attr.getDimensionPixelSize(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionHighlightTextSize, convertDpToPx(context, 10f))
        val sectionHighlightTextStyle = attr.getInt(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionHighlightTextStyle, 1)
        sectionHighlightTextTypeface = convertAttrTextStyleToTypeface(sectionHighlightTextStyle)
        sectionHighlightTextColor = attr.getColor(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionHighlightTextColor, Color.WHITE)
        isHighlightEnable = attr.getBoolean(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionHighlightEnable, false)

        sectionPreviewViewWidth = attr.getDimensionPixelSize(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionPreviewWidth, convertDpToPx(context, 56f))
        sectionPreviewViewHeight = attr.getDimensionPixelSize(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionPreviewHeight, convertDpToPx(context, 56f))
        sectionPreviewBackgroundDrawable = attr.getDrawable(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionPreviewBackground)
        if (sectionPreviewBackgroundDrawable == null) {
            sectionPreviewBackgroundDrawable = ColorDrawable(Color.argb(0x80, 0, 0, 0))
        }
        val sectionPreviewTextStyle = attr.getInt(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionPreviewTextStyle, 0)
        sectionPreviewTextTypeface = convertAttrTextStyleToTypeface(sectionPreviewTextStyle)
        sectionPreviewTextSize = attr.getDimensionPixelSize(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionPreviewTextSize, convertDpToPx(context, 40f))
        sectionPreviewTextColor = attr.getColor(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionPreviewTextColor, Color.WHITE)
        isSectionPreviewEnable = attr.getBoolean(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionPreviewEnable, true)
        sectionPreviewHideDelay = attr.getInt(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionPreviewHideDelay, 300)

        attr.recycle()

        if (background == null) {
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

        requestLayout()
    }

    private fun convertAttrTextStyleToTypeface(textStyle: Int): Typeface {
        return when (textStyle) {
            1 -> Typeface.DEFAULT_BOLD
            else -> Typeface.DEFAULT
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // If down event occurs inside section bar region, start indexing
                if (isSectionsRegionPoint(event.x, event.y)) {
                    recyclerView?.stopScroll()

                    isIndexing = true
                    // Determine which section the point is in, and move the list to that section
                    val sectionIndex = getSectionIndexByPoint(event.y)
                    setCurrentSectionIndex(sectionIndex)
                    scrollToSection(sectionIndex)
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isIndexing) {
                    // If this event moves inside index bar
                    if (isSectionsRegionPoint(event.x, event.y)) {
                        // Determine which section the point is in, and move the list to that section
                        val sectionIndex = getSectionIndexByPoint(event.y)
                        setCurrentSectionIndex(sectionIndex)
                        scrollToSection(sectionIndex)
                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isIndexing) {
                    isIndexing = false
                }
            }
        }
        return false
    }

    private fun isSectionsRegionPoint(x: Float, y: Float): Boolean {
        // Determine if the point is in index bar region, which includes the right margin of the bar
        return (x >= sectionBarRect.left && y >= sectionBarRect.top && y <= sectionBarRect.top + sectionBarRect.height())
    }

    fun setSectionBarWidth(width: Int) {
        this.sectionBarWidth = width

        invalidate()
    }

    fun setSectionBarBackground(drawable: Drawable?) {
        sectionBarBackgroundDrawable = drawable

        invalidate()
    }

    fun setCurrentSectionIndex(index: Int) {
        currentSectionIndex = index
        invalidate()
    }

    fun getCurrentSectionIndex(): Int {
        return currentSectionIndex
    }

    private fun getCurrentSection(): String {
        if (0 <= currentSectionIndex && currentSectionIndex < (sections?.size ?: 0)) {
            return sections?.get(currentSectionIndex) ?: ""
        }
        return ""
    }

    fun setSectionPreviewSize(width: Int, height: Int) {
        this.sectionPreviewViewWidth = width
        this.sectionPreviewViewHeight = height

        if (isSectionPreviewEnable) {
            invalidate()
        }
    }

    fun setSectionPreviewBackground(drawable: Drawable?) {
        sectionPreviewBackgroundDrawable = drawable

        if (isSectionPreviewEnable) {
            invalidate()
        }
    }

    fun setSectionPreviewEnable(enable: Boolean) {
        isSectionPreviewEnable = enable

        invalidate()
    }

    private fun getSectionByRecyclerView(recyclerView: RecyclerView?): Int {
        recyclerView ?: return RecyclerView.NO_POSITION

        // updates current section
        val barHeight = sectionBarRect.height()
        if (barHeight <= 0) {
            return RecyclerView.NO_POSITION
        }

        val offset = recyclerView.computeVerticalScrollOffset()
        val extent = recyclerView.computeVerticalScrollExtent()
        val range = recyclerView.computeVerticalScrollRange()

        val percentage = (100.0f * offset / (range - extent).toFloat())

//        Log.d("Herry", "current percentage = $percentage, offset = $offset, extent = $extent, range = $range, barHeight = $barHeight")
//        val y = barHeight * (percentage / 100.0f)

        val y = (offset.toFloat() / (range - extent - extent)) * barHeight
        Log.d("Herry", "current percentage = $percentage, y = $y")
        return getSectionIndexByPoint(y)
    }

    fun attachRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        this.recyclerView?.run {
//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    if (!isIndexing) {
//                        setCurrentSectionIndex(getSectionByRecyclerView(recyclerView))
//                    }
//                }
//            })
            if (recyclerView.adapter != null) attachAdapter(recyclerView.adapter)
        }
    }

    private fun attachAdapter(adapter: RecyclerView.Adapter<*>?) {
        if (this.adapter == adapter) return
        this.adapter?.unregisterAdapterDataObserver(adapterObserver)
        adapter?.registerAdapterDataObserver(adapterObserver)
        this.adapter = adapter

        if (adapter is SectionIndexer) {
            sectionIndexer = adapter
            updateSections()
        }
    }

    private fun updateSections() {
        @Suppress("UNCHECKED_CAST")
        sections = sectionIndexer?.sections as? Array<String>
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        savedState?.run {
            setCurrentSectionIndex(this.currentSectionIndex)
            savedState = null
        }

        sectionBarRect = getSectionBarRect()
    }

    private fun getSectionBarRect(): RectF {
        val left = (width - sectionBarWidth - paddingLeft).toFloat()
        val right = width.toFloat() - paddingRight
        val top = 0f
        val bottom = this.height.toFloat()

        return RectF(left, top, right, bottom)
    }

    private fun getSectionIndexByPoint(y: Float): Int {

        Log.d("Herry", "getSectionIndexByPoint y = $y")
        val sections = this.sections
        if (sections == null || sections.isEmpty()) return RecyclerView.NO_POSITION

        return when {
            y < sectionBarRect.top + paddingTop -> {
                0
            }
            y >= sectionBarRect.top + sectionBarRect.height() - paddingBottom -> {
                sections.size - 1
            }
            else -> {
                ((y - sectionBarRect.top - paddingTop) / ((sectionBarRect.height() - (paddingTop + paddingBottom)) / sections.size)).toInt()
            }
        }
    }

    private fun scrollToSection(sectionIndex: Int) {
        try {
            val position = sectionIndexer?.getPositionForSection(sectionIndex) ?: return
            val layoutManager: RecyclerView.LayoutManager = recyclerView?.layoutManager ?: return
            if (layoutManager is LinearLayoutManager) {
                layoutManager.scrollToPositionWithOffset(position, 0)
            } else {
                layoutManager.scrollToPosition(position)
            }
        } catch (e: Exception) {
            Log.d("RVAISV", "Data size returns null")
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return

        // draws FrameLayout attributes
        super.onDraw(canvas)

        drawSectionBar(canvas)
        drawSections(canvas)
        drawSectionPreview(canvas)
    }

    private fun drawSectionBar(canvas: Canvas) {
        canvas.save()

        sectionBarBackgroundDrawable?.bounds = sectionBarRect.toRect()
        sectionBarBackgroundDrawable?.draw(canvas)

        canvas.restore()
    }

    private fun drawSections(canvas: Canvas) {
        canvas.save()
        sectionTextPaint.reset()
        sectionTextPaint.isAntiAlias = true

        val currentSectionIndex = getCurrentSectionIndex()
        val sections = sections ?: return
        if (sections.isEmpty()) {
            return
        }

        val sectionBarHeight = sectionBarRect.height() - (paddingTop + paddingBottom)
        val sectionHeight: Float = sectionBarHeight / sections.size

        var lastSectionTextY = -1f
        sections.forEachIndexed { index, section ->
            val isCurrentSection = currentSectionIndex > -1 && index == currentSectionIndex
            if (isHighlightEnable && isCurrentSection) {
                sectionTextPaint.textSize = sectionHighlightTextSize.toFloat()
                sectionTextPaint.typeface = sectionHighlightTextTypeface
                sectionTextPaint.color = sectionHighlightTextColor
            } else {
                sectionTextPaint.textSize = sectionTextSize.toFloat()
                sectionTextPaint.typeface = sectionTextTypeface
                sectionTextPaint.color = sectionTextColor
            }

            val sectionTextTop: Float = (sectionHeight - (sectionTextPaint.descent() - sectionTextPaint.ascent())) / 2f
            val sectionTextY = sectionBarRect.top + paddingTop + sectionHeight * index + sectionTextTop - sectionTextPaint.ascent()
            val sectionTextHeight = sectionTextPaint.descent() - sectionTextPaint.ascent()

            if (lastSectionTextY == -1f || (lastSectionTextY + sectionTextHeight) < sectionTextY) {
                lastSectionTextY = sectionTextY

                val sectionTextStart: Float = (sectionBarWidth - sectionTextPaint.measureText(section)) / 2f
                val sectionTextX = (if (!isRTL()) sectionBarRect.left else sectionBarRect.right) + sectionTextStart
                canvas.drawText(section, sectionTextX, sectionTextY, sectionTextPaint)
            }
        }
        canvas.restore()
    }

    private fun drawSectionPreview(canvas: Canvas) {
        canvas.save()

        val section = getCurrentSection()
        // Preview is shown when mCurrentSection is set
        if (isIndexing && isSectionPreviewEnable && section.isNotBlank()) {
            // draws section preview text
            sectionPreviewTextPaint.reset()
            sectionPreviewTextPaint.color = sectionPreviewTextColor
            sectionPreviewTextPaint.isAntiAlias = true
            sectionPreviewTextPaint.textSize = sectionPreviewTextSize.toFloat()
            sectionPreviewTextPaint.typeface = sectionPreviewTextTypeface

            val sectionPreviewTextWidth: Float = sectionPreviewTextPaint.measureText(section)
            val sectionPreviewTextHeight: Float = (sectionPreviewTextPaint.descent() - sectionPreviewTextPaint.ascent())

            val previewWidth: Float = kotlin.math.max(sectionPreviewTextWidth, sectionPreviewViewWidth.toFloat())
            val previewHeight: Float = kotlin.math.max(sectionPreviewTextHeight, sectionPreviewViewHeight.toFloat())

            // draws background
            val sectionPreviewRect = RectF(
                (width - previewWidth) / 2f,
                (height - previewHeight) / 2f,
                (width - previewWidth) / 2f + previewWidth,
                (height - previewHeight) / 2 + previewHeight
            )

            sectionPreviewBackgroundDrawable?.bounds = sectionPreviewRect.toRect()
            sectionPreviewBackgroundDrawable?.draw(canvas)

            val textLeft = ((if (isRTL()) sectionPreviewRect.right else sectionPreviewRect.left) + ((sectionPreviewRect.width() - sectionPreviewTextWidth) / 2f) + 0.5f).toInt().toFloat()
            val textTop = (sectionPreviewRect.top + ((sectionPreviewRect.height() - sectionPreviewTextHeight) / 2f) + 0.5f).toInt().toFloat()

            canvas.drawText(
                section,
                textLeft,
                textTop - sectionPreviewTextPaint.ascent(),
                sectionPreviewTextPaint
            )

            hideSectionPreview()
        }

        canvas.restore()
    }

    private fun hideSectionPreview(delay: Long = sectionPreviewHideDelay.toLong()) {
        if (isSectionPreviewEnable) {
            removeCallbacks(sectionPreviewHideRunnable)
            postDelayed(sectionPreviewHideRunnable, delay)
        }
    }

    private fun isRTL(): Boolean {
        context ?: return false

        return context.resources.configuration.layoutDirection == LAYOUT_DIRECTION_RTL
    }

    private fun convertDpToPx(context: Context?, dp: Float): Int {
        return if (null == context) {
            0
        } else (dp * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.currentSectionIndex = currentSectionIndex
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState: SavedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        this.savedState = savedState
        requestLayout()
    }

    internal class SavedState : BaseSavedState {
        var currentSectionIndex = 0

        constructor(superState: Parcelable?) : super(superState) {}
        constructor(source: Parcel) : super(source) {
            currentSectionIndex = source.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(currentSectionIndex)
        }

        override fun toString(): String {
            return ("SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " currentSectionIndex=" + currentSectionIndex + "}")
        }
    }
}
package com.herry.libs.widget.recyclerview.scroller

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SectionIndexer
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRect
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.herry.libs.R
import kotlin.math.roundToInt

@Suppress("MemberVisibilityCanBePrivate", "unused")
class RecyclerViewAlphabetIndexerScrollerView : FrameLayout {

    private var sectionBarView: View
    private var sectionPreviewView: View? = null
    private var recyclerView: RecyclerView? = null

    private var adapter: RecyclerView.Adapter<*>? = null
    private val adapterObserver: RecyclerView.AdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            updateSections()
            setCurrentSectionIndex(getSectionByRecyclerView(this@RecyclerViewAlphabetIndexerScrollerView.recyclerView).toInt())
        }
    }

    private var sectionIndexer: SectionIndexer? = null
    private var sections: Array<String>? = null
    private var currentSectionIndex: Int = -1

    private var sectionBarBackgroundDrawable: Drawable? = null
    private var sectionBarPaddingTop: Int = 0
    private var sectionBarPaddingBottom: Int = 0

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
        val sectionBarWidth = attr.getDimensionPixelSize(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionBarWidth, convertDpToPx(context, 30f))
        sectionBarPaddingTop = attr.getDimensionPixelSize(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionBarPaddingTop, convertDpToPx(context, 8f))
        sectionBarPaddingBottom = attr.getDimensionPixelSize(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionBarPaddingBottom, convertDpToPx(context, 8f))

        val sectionTextStyle = attr.getInt(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionTextStyle, 0)
        sectionTextTypeface = convertAttrTextStyleToTypeface(sectionTextStyle)
        sectionTextColor = attr.getColor(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionTextColor, Color.WHITE)
        sectionTextSize = attr.getDimensionPixelSize(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionTextSize, convertDpToPx(context, 10f))

        sectionHighlightTextSize = attr.getDimensionPixelSize(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionHighlightTextSize, convertDpToPx(context, 10f))
        val sectionHighlightTextStyle = attr.getInt(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionHighlightTextStyle, 1)
        sectionHighlightTextTypeface = convertAttrTextStyleToTypeface(sectionHighlightTextStyle)
        sectionHighlightTextColor = attr.getColor(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionHighlightTextColor, Color.WHITE)
        isHighlightEnable = attr.getBoolean(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionHighlightEnable, false)

        val sectionPreviewViewWidth = attr.getDimensionPixelSize(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionPreviewWidth, convertDpToPx(context, 56f))
        val sectionPreviewViewHeight = attr.getDimensionPixelSize(R.styleable.RecyclerViewAlphabetIndexerScrollerView_rvaisv_sectionPreviewHeight, convertDpToPx(context, 56f))
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

        sectionBarView = View(context).apply {
            setOnTouchListener(object : OnTouchListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    v ?: return false
                    event ?: return false

                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            // If down event occurs inside section bar region, start indexing
                            recyclerView?.stopScroll()

                            isIndexing = true
                            // Determine which section the point is in, and move the list to that section
                            scrollToSection(getSectionByPoint(event.y).toInt())
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (isIndexing) {
                                // Determine which section the point is in, and move the list to that section
                                scrollToSection(getSectionByPoint(event.y).toInt())
                                return true
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                            if (isIndexing) {
                                isIndexing = false
                            }
                        }
                    }
                    return true
                }
            })
        }
        addView(sectionBarView)

        sectionPreviewView = View(context)
        addView(sectionPreviewView)

        setSectionBarWidth(sectionBarWidth)
        setSectionPreviewSize(sectionPreviewViewWidth, sectionPreviewViewHeight)

        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
    }

    private fun convertAttrTextStyleToTypeface(textStyle: Int): Typeface {
        return when (textStyle) {
            1 -> Typeface.DEFAULT_BOLD
            else -> Typeface.DEFAULT
        }
    }

    fun setSectionBarWidth(width: Int) {
        sectionBarView.layoutParams = LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT, GravityCompat.END)

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
        sectionPreviewView?.layoutParams = LayoutParams(width, height, Gravity.CENTER)

        if (isSectionPreviewEnable) {
            invalidate()
        }
    }

    private fun getSectionPreviewRect(): RectF {
        val sectionPreviewView = this.sectionPreviewView ?: return RectF()
        return RectF(
            sectionPreviewView.x,
            sectionPreviewView.y,
            sectionPreviewView.x + sectionPreviewView.width,
            sectionPreviewView.y + sectionPreviewView.height
        )
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

    private fun getSectionByRecyclerView(recyclerView: RecyclerView?): Float {
        recyclerView ?: return 0f

        // updates current section
        val barHeight = sectionBarView.height
        if (barHeight <= 0) {
            return 0f
        }
        val scrollOffset = recyclerView.computeVerticalScrollOffset()
        val verticalScrollRange = recyclerView.computeVerticalScrollRange() + recyclerView.paddingBottom
        val calculatedHandleHeight = (barHeight.toFloat() / verticalScrollRange * barHeight).toInt()
        if (calculatedHandleHeight >= barHeight) {
            return 0f
        }
        val ratio = scrollOffset.toFloat() / (verticalScrollRange - barHeight)
        val y = ratio * (barHeight - calculatedHandleHeight)
        return getSectionByPoint(y)
    }

    fun attachRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        this.recyclerView?.run {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    setCurrentSectionIndex(getSectionByRecyclerView(recyclerView).toInt())
                }
            })
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

    private fun getSectionBarRect(): RectF {
        return RectF(
            this.sectionBarView.x,
            this.sectionBarView.y,
            this.sectionBarView.x + this.sectionBarView.width,
            this.sectionBarView.y + this.sectionBarView.height
        )
    }

    private fun getSectionByPoint(y: Float): Float {
        val sections = this.sections
        if (sections == null || sections.isEmpty()) return 0f

        val sectionBarRect = getSectionBarRect()

        if (y < sectionBarRect.top) return 0f
        return if (y >= sectionBarRect.top + sectionBarRect.height()) {
            (sections.size - 1).toFloat()
        } else {
            ((y - sectionBarRect.top) / ((sectionBarRect.height()) / sections.size))
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
        val sectionBarRect = getSectionBarRect()
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

        val sectionBarRect = getSectionBarRect()
        val sectionHeight: Float = (sectionBarRect.height() -  sectionBarPaddingTop -  sectionBarPaddingBottom) / sections.size

        // calculates maximum section text height
        if (isHighlightEnable) {
            sectionTextPaint.textSize = sectionHighlightTextSize.toFloat()
            sectionTextPaint.typeface = sectionHighlightTextTypeface
            sectionTextPaint.color = sectionHighlightTextColor
        } else {
            sectionTextPaint.textSize = sectionTextSize.toFloat()
            sectionTextPaint.typeface = sectionTextTypeface
            sectionTextPaint.color = sectionTextColor
        }
        val maximumSectionTextHeight = (sectionTextPaint.descent() - sectionTextPaint.ascent())

        var indexStep = 1
        if (sectionHeight < maximumSectionTextHeight) {
            indexStep = (maximumSectionTextHeight / sectionHeight).roundToInt()
        }

        for (index in sections.indices step indexStep) {
            val section = sections[index]
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

            val sectionTextTop: Float = (sectionHeight - (sectionTextPaint.descent() - sectionTextPaint.ascent())) / 2
            val sectionTextStart: Float = (sectionBarRect.width() - sectionTextPaint.measureText(section)) / 2

            canvas.drawText(
                section,
                (if (!isRTL()) sectionBarRect.left else sectionBarRect.right) + sectionTextStart,
                sectionBarRect.top + sectionBarPaddingTop + sectionHeight * index + sectionTextTop - sectionTextPaint.ascent(),
                sectionTextPaint
            )
        }
        canvas.restore()
    }

    private fun drawSectionPreview(canvas: Canvas) {
        canvas.save()

        val section = getCurrentSection()
        // Preview is shown when mCurrentSection is set
        if (isIndexing && isSectionPreviewEnable && section.isNotBlank()) {
            // draws background
            val sectionPreviewRect = getSectionPreviewRect()
            sectionPreviewBackgroundDrawable?.bounds = sectionPreviewRect.toRect()
            sectionPreviewBackgroundDrawable?.draw(canvas)

            canvas.restore()
            canvas.save()

            // draws section preview text
            sectionPreviewTextPaint.reset()
            sectionPreviewTextPaint.color = sectionPreviewTextColor
            sectionPreviewTextPaint.isAntiAlias = true
            sectionPreviewTextPaint.textSize = sectionPreviewTextSize.toFloat()
            sectionPreviewTextPaint.typeface = sectionPreviewTextTypeface

            val sectionPreviewTextWidth: Float = sectionPreviewTextPaint.measureText(section)
            val sectionPreviewTextHeight: Float = (sectionPreviewTextPaint.descent() - sectionPreviewTextPaint.ascent())

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
}
package com.herry.libs.widget.recyclerview.scroller

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.herry.libs.R


@Suppress("unused", "MemberVisibilityCanBePrivate")
class RecyclerViewFastScrollerView : FrameLayout {

    companion object {
        private const val DEFAULT_AUTO_HIDE_DELAY = 1500
    }

    private var barView: View
    private var handleView: View

    private val hideRunnable: Runnable
    private val minScrollHandleHeight: Int

    private var recyclerView: RecyclerView? = null
    private var animatorSet: AnimatorSet? = null
    private var isAnimatingIn = false

    private var hideDelay = 0
    private var isHidingEnabled = false
    private var isShowHandle = true

    @ColorInt
    private var handleNormalColor = 0

    @ColorInt
    private var handlePressedColor = 0

    @ColorInt
    private var barColor = 0
    private var touchTargetWidth = 0
    private var barInset = 0
    private var handleWidth = 0
    private var handleMinHeight = 0
    private var handleInsetWidth = 0
    private var isHideOverride = false

    private var adapter: RecyclerView.Adapter<*>? = null
    private val adapterObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            requestLayout()
        }
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        val attr = context.obtainStyledAttributes(attrs, R.styleable.RecyclerViewFastScrollerView)
        barColor = attr.getColor(R.styleable.RecyclerViewFastScrollerView_rvfsv_barColor, resolveColor(context, R.attr.colorControlNormal))
        handleNormalColor = attr.getColor(R.styleable.RecyclerViewFastScrollerView_rvfsv_handleNormalColor, resolveColor(context, R.attr.colorControlNormal))
        handlePressedColor = attr.getColor(R.styleable.RecyclerViewFastScrollerView_rvfsv_handlePressedColor, resolveColor(context, R.attr.colorAccent))
        touchTargetWidth = attr.getDimensionPixelSize(R.styleable.RecyclerViewFastScrollerView_rvfsv_touchTargetWidth, convertDpToPx(context, 24f))
        handleWidth = attr.getDimensionPixelSize(R.styleable.RecyclerViewFastScrollerView_rvfsv_handleWidth, convertDpToPx(context, 24f))
        handleMinHeight = attr.getDimensionPixelSize(R.styleable.RecyclerViewFastScrollerView_rvfsv_handleMinHeight, convertDpToPx(context, 48f))
        handleInsetWidth = attr.getDimensionPixelSize(R.styleable.RecyclerViewFastScrollerView_rvfsv_handleInsetWidth, convertDpToPx(context, 8f))
        hideDelay = attr.getInt(R.styleable.RecyclerViewFastScrollerView_rvfsv_hideDelay, DEFAULT_AUTO_HIDE_DELAY)
        isHidingEnabled = attr.getBoolean(R.styleable.RecyclerViewFastScrollerView_rvfsv_hidingEnabled, true)
        isShowHandle = attr.getBoolean(R.styleable.RecyclerViewFastScrollerView_rvfsv_showHandle, true)
        attr.recycle()

        if (0 < handleWidth) {
            layoutParams = ViewGroup.LayoutParams(handleWidth, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        barView = View(context)
        handleView = View(context)
        handleView.isVisible = isShowHandle

        addView(barView)
        addView(handleView)

        setTouchTargetWidth(touchTargetWidth)

        minScrollHandleHeight = handleMinHeight
        hideRunnable = Runnable {
            if (!handleView.isPressed) {
                if (animatorSet?.isStarted == true) {
                    animatorSet?.cancel()
                }
                animatorSet = AnimatorSet()
                val animator2 = ObjectAnimator.ofFloat(
                    this@RecyclerViewFastScrollerView, ALPHA,
                    1f, 0f
                )
                animator2.interpolator = FastOutLinearInInterpolator()
                animator2.duration = 150
                handleView.isEnabled = false
                animatorSet?.play(animator2)
                animatorSet?.start()
            }
        }

        barView.setOnTouchListener(object : OnTouchListener {
            private var initialBarHeight = 0f
            private var lastPressedYAdjustedToInitial = 0f

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                v ?: return false
                event ?: return false

                val recyclerView = this@RecyclerViewFastScrollerView.recyclerView ?: return false

                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        handleView.isPressed = true

                        recyclerView.stopScroll()
                        var nestedScrollAxis = ViewCompat.SCROLL_AXIS_NONE
                        nestedScrollAxis = nestedScrollAxis or ViewCompat.SCROLL_AXIS_VERTICAL
                        recyclerView.startNestedScroll(nestedScrollAxis)

                        initialBarHeight = barView.height.toFloat()
                        lastPressedYAdjustedToInitial = event.y + barView.y

                        val deltaPressedYFromLastAdjustedToInitial = event.y - handleView.y
                        val dY = (deltaPressedYFromLastAdjustedToInitial / initialBarHeight *
                                recyclerView.computeVerticalScrollRange()).toInt()
                        updateRecyclerViewScroll(dY)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val newHandlePressedY = event.y + barView.y
                        val barHeight = barView.height
                        val newHandlePressedYAdjustedToInitial = newHandlePressedY + (initialBarHeight - barHeight)
                        val deltaPressedYFromLastAdjustedToInitial = newHandlePressedYAdjustedToInitial - lastPressedYAdjustedToInitial
                        val dY = (deltaPressedYFromLastAdjustedToInitial / initialBarHeight *
                                recyclerView.computeVerticalScrollRange()).toInt()
                        updateRecyclerViewScroll(dY)

                        lastPressedYAdjustedToInitial = newHandlePressedYAdjustedToInitial
                    }
                    MotionEvent.ACTION_UP -> {
                        lastPressedYAdjustedToInitial = -1f
                        recyclerView.stopNestedScroll()
                        handleView.isPressed = false
                        postAutoHide()
                    }
                }
                return true
            }
        })

        handleView.setOnTouchListener(object : OnTouchListener {
            private var initialBarHeight = 0f
            private var lastPressedYAdjustedToInitial = 0f

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                v ?: return false
                event ?: return false

                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        handleView.isPressed = true
                        recyclerView?.let { recyclerView ->
                            recyclerView.stopScroll()
                            var nestedScrollAxis = ViewCompat.SCROLL_AXIS_NONE
                            nestedScrollAxis = nestedScrollAxis or ViewCompat.SCROLL_AXIS_VERTICAL
                            recyclerView.startNestedScroll(nestedScrollAxis)
                        }

                        initialBarHeight = barView.height.toFloat()
                        lastPressedYAdjustedToInitial = event.y + handleView.y + barView.y
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val newHandlePressedY = event.y + handleView.y + barView.y
                        val barHeight = barView.height
                        val newHandlePressedYAdjustedToInitial = newHandlePressedY + (initialBarHeight - barHeight)
                        val deltaPressedYFromLastAdjustedToInitial = newHandlePressedYAdjustedToInitial - lastPressedYAdjustedToInitial
                        val dY = (deltaPressedYFromLastAdjustedToInitial / initialBarHeight *
                                (recyclerView?.computeVerticalScrollRange() ?: 0)).toInt()
                        updateRecyclerViewScroll(dY)
                        lastPressedYAdjustedToInitial = newHandlePressedYAdjustedToInitial
                    }
                    MotionEvent.ACTION_UP -> {
                        lastPressedYAdjustedToInitial = -1f
                        recyclerView?.stopNestedScroll()
                        handleView.isPressed = false
                        postAutoHide()
                    }
                }
                return true
            }
        })
        alpha = 1f
    }

    fun getHideDelay(): Int {
        return hideDelay
    }

    /**
     * @param hideDelay the delay in millis to hide the scrollbar
     */
    fun setHideDelay(hideDelay: Int) {
        this.hideDelay = hideDelay
    }

    @ColorInt
    fun getHandlePressedColor(): Int = handlePressedColor

    fun setHandlePressedColor(@ColorInt color: Int) {
        handlePressedColor = color
        updateHandleColorsAndInset()
    }

    @ColorInt
    fun getHandleNormalColor(): Int = handleNormalColor

    fun setHandleNormalColor(@ColorInt color: Int) {
        handleNormalColor = color
        updateHandleColorsAndInset()
    }

    @ColorInt
    fun getBarColor(): Int = barColor

    /**
     * @param color Scroll bar color.
     * Alpha will be set to ~22% to match stock scrollbar.
     */
    fun setBarColor(@ColorInt color: Int) {
        barColor = color
        updateBarColorAndInset()
    }

    fun getTouchTargetWidth(): Int = touchTargetWidth

    /**
     * @param width In pixels, less than or equal to 48dp
     */
    fun setTouchTargetWidth(width: Int) {
        touchTargetWidth = width
        barInset = touchTargetWidth - handleInsetWidth
        if (touchTargetWidth > handleWidth) {
            throw RuntimeException("Touch target width cannot be larger than 48dp!")
        }
        barView.layoutParams = LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT, GravityCompat.END)
        handleView.layoutParams = LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT, GravityCompat.END)

        updateHandleColorsAndInset()
        updateBarColorAndInset()
    }

    fun isHidingEnabled(): Boolean = isHidingEnabled

    /**
     * @param enabled whether hiding is enabled
     */
    fun setHidingEnabled(enabled: Boolean) {
        isHidingEnabled = enabled
        if (enabled) {
            postAutoHide()
        }
    }

    private fun updateHandleColorsAndInset() {
        val drawable = StateListDrawable()
        if (!isRTL(context)) {
            drawable.addState(
                PRESSED_ENABLED_STATE_SET,
                InsetDrawable(ColorDrawable(handlePressedColor), barInset, 0, 0, 0)
            )
            drawable.addState(
                EMPTY_STATE_SET,
                InsetDrawable(ColorDrawable(handleNormalColor), barInset, 0, 0, 0)
            )
        } else {
            drawable.addState(
                PRESSED_ENABLED_STATE_SET,
                InsetDrawable(ColorDrawable(handlePressedColor), 0, 0, barInset, 0)
            )
            drawable.addState(
                EMPTY_STATE_SET,
                InsetDrawable(ColorDrawable(handleNormalColor), 0, 0, barInset, 0)
            )
        }
        setViewBackground(handleView, drawable)
    }

    private fun updateBarColorAndInset() {
        val drawable: Drawable = if (!isRTL(context)) {
            InsetDrawable(ColorDrawable(barColor), barInset, 0, 0, 0)
        } else {
            InsetDrawable(ColorDrawable(barColor), 0, 0, barInset, 0)
        }

        drawable.alpha = 57

        setViewBackground(barView, drawable)
    }

    fun attachRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        this.recyclerView?.run {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    show()
                }
            })
            if (recyclerView.adapter != null) attachAdapter(recyclerView.adapter)
        }
    }

    fun attachAdapter(adapter: RecyclerView.Adapter<*>?) {
        if (this.adapter == adapter) return
        this.adapter?.unregisterAdapterDataObserver(adapterObserver)
        adapter?.registerAdapterDataObserver(adapterObserver)
        this.adapter = adapter
    }

    /**
     * Show the fast scroller and hide after delay
     *
     * @param animate whether to animate showing the scroller
     */
    fun show(animate: Boolean = true) {
        requestLayout()
        post(Runnable {
            if (isHideOverride) {
                return@Runnable
            }
            handleView.isEnabled = true
            if (animate) {
                if (!isAnimatingIn && alpha != 1f) {
                    if (animatorSet?.isStarted == true) {
                        animatorSet?.cancel()
                    }
                    animatorSet = AnimatorSet()
                    val animator = ObjectAnimator.ofFloat(this@RecyclerViewFastScrollerView, ALPHA, 0f, 1f)
                    animator.interpolator = LinearOutSlowInInterpolator()
                    animator.duration = 100
                    animator.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            isAnimatingIn = false
                        }
                    })
                    isAnimatingIn = true
                    animatorSet?.play(animator)
                    animatorSet?.start()
                }
            } else {
                alpha = 1f
            }
            postAutoHide()
        })
    }

    fun hide() {
        postAutoHide(0L)
    }

    private fun postAutoHide(delay: Long = hideDelay.toLong()) {
        if (isHidingEnabled) {
            removeCallbacks(hideRunnable)
            postDelayed(hideRunnable, delay)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val recyclerView = this.recyclerView ?: return

        val scrollOffset = recyclerView.computeVerticalScrollOffset()
        val verticalScrollRange = recyclerView.computeVerticalScrollRange() + recyclerView.paddingBottom
        val barHeight = barView.height
        var calculatedHandleHeight = (barHeight.toFloat() / verticalScrollRange * barHeight).toInt()
        if (calculatedHandleHeight < minScrollHandleHeight) {
            calculatedHandleHeight = minScrollHandleHeight
        }
        if (calculatedHandleHeight >= barHeight) {
            alpha = 1f
            isHideOverride = true
            return
        }
        isHideOverride = false
        val ratio = scrollOffset.toFloat() / (verticalScrollRange - barHeight)
        val y = ratio * (barHeight - calculatedHandleHeight)
        handleView.layout(handleView.left, y.toInt(), handleView.right, y.toInt() + calculatedHandleHeight)
    }

    private fun updateRecyclerViewScroll(dY: Int) {
        try {
            recyclerView?.scrollBy(0, dY)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun setViewBackground(view: View?, background: Drawable) {
        if (null == view) {
            return
        }
        view.background = background
    }

    private fun isRTL(context: Context?): Boolean {
        context ?: return false

        return context.resources.configuration.layoutDirection == LAYOUT_DIRECTION_RTL
    }

    @ColorInt
    private fun resolveColor(context: Context?, @AttrRes color: Int): Int {
        if (null == context) {
            return 0
        }
        val a = context.obtainStyledAttributes(intArrayOf(color))
        val resId = a.getColor(0, 0)
        a.recycle()
        return resId
    }

    private fun convertDpToPx(context: Context?, dp: Float): Int {
        return if (null == context) {
            0
        } else (dp * context.resources.displayMetrics.density + 0.5f).toInt()
    }
}

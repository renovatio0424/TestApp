package com.herry.libs.widget.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import com.herry.libs.R

@Suppress("MemberVisibilityCanBePrivate", "RedundantOverride", "ProtectedInFinal")
class RoundedFrameLayout : FrameLayoutEx {

    companion object {
        private const val DEFAULT_CORNER_RADIUS = 0f
        private const val CORNER_TOP = 0x01
        private const val CORNER_BOTTOM = 0x02
        private const val CORNER_START = 0x04
        private const val CORNER_END = 0x08
        private const val CORNER_DEFAULT = CORNER_START or CORNER_END or CORNER_TOP or CORNER_BOTTOM
    }

    private var baseClipPath: Path? = null
    private var cornerRadiusRadiusX = DEFAULT_CORNER_RADIUS
    private var cornerRadiusRadiusY = DEFAULT_CORNER_RADIUS
    private var cornerDimensions: FloatArray? = null
    private val backgroundPaint = Paint()
    private var isCircle = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        val attr = context.obtainStyledAttributes(attrs, R.styleable.RoundedFrameLayout)

        // set attributes
        val cornerRadius = attr.getDimensionPixelSize(R.styleable.RoundedFrameLayout_rfCornerRadius, 0).toFloat()
        val corners = attr.getInt(R.styleable.RoundedFrameLayout_rfCorners, CORNER_DEFAULT)
        setCornerRadius(cornerRadius, corners)
        isCircle = attr.getBoolean(R.styleable.RoundedFrameLayout_rfIsCircle, false)

        var backgroundColor = 0
        if (attr.hasValue(R.styleable.RoundedFrameLayout_rfBackgroundColor)) {
            backgroundColor = attr.getColor(R.styleable.RoundedFrameLayout_rfBackgroundColor, 0)
        }
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.color = backgroundColor

        val strokeColor = attr.getColor(R.styleable.RoundedFrameLayout_rfStrokeColor, -1)
        val strokeWidth = attr.getDimensionPixelSize(R.styleable.RoundedFrameLayout_rfStrokeWidth, 0)

        attr.recycle()

        foreground = createForegroundDrawable(strokeColor, strokeWidth)
    }

    private fun createForegroundDrawable(strokeColor: Int, strokeWidth: Int): Drawable {
        val fgDrawable = GradientDrawable()
        fgDrawable.setColor(Color.TRANSPARENT)
        fgDrawable.cornerRadius = this.getCornerRadius()
        if (strokeColor != -1) {
            fgDrawable.setStroke(strokeWidth, strokeColor)
        }

        return fgDrawable
    }

    override fun dispatchDraw(canvas: Canvas) {
        baseClipPath = baseClipPath ?: Path()
        baseClipPath?.run {
            reset()

            if (isCircle) {
                val radius = canvas.width.toFloat().coerceAtMost(canvas.height.toFloat()) / 2f
                addRoundRect(
                    RectF(
                        0f,
                        0f,
                        canvas.width.toFloat(),
                        canvas.height.toFloat()
                    ),
                    radius,
                    radius,
                    Path.Direction.CW
                )
            } else {
                cornerDimensions?.let { corner ->
                    addRoundRect(
                        RectF(
                            0f,
                            0f,
                            canvas.width.toFloat(),
                            canvas.height.toFloat()
                        ),
                        corner,
                        Path.Direction.CW
                    )
                } ?: run {
                    addRoundRect(
                        RectF(
                            0f,
                            0f,
                            canvas.width.toFloat(),
                            canvas.height.toFloat()
                        ),
                        cornerRadiusRadiusX,
                        cornerRadiusRadiusY,
                        Path.Direction.CW
                    )
                }

                canvas.drawRoundRect(
                    RectF(
                        0f,
                        0f,
                        canvas.width.toFloat(),
                        canvas.height.toFloat()
                    ),
                    cornerRadiusRadiusX,  // rx
                    cornerRadiusRadiusY,  // ry
                    backgroundPaint // Paint
                )
            }
            canvas.clipPath(this)
        }
        super.dispatchDraw(canvas)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        requestLayout()
    }

    fun setCornerRadius(radius: Float, corners: Int) {
        setCornerRadius(radius, radius, corners)
    }

    fun setCornerRadius(rx: Float, ry: Float, corners: Int) {
        var topStartCornerRadiusX = 0f
        var topStartCornerRadiusY = 0f
        if (corners and (CORNER_TOP or CORNER_START) == CORNER_TOP or CORNER_START) {
            topStartCornerRadiusX = rx
            topStartCornerRadiusY = ry
        }
        var topEndCornerRadiusX = 0f
        var topEndCornerRadiusY = 0f
        if (corners and (CORNER_TOP or CORNER_END) == CORNER_TOP or CORNER_END) {
            topEndCornerRadiusX = rx
            topEndCornerRadiusY = ry
        }
        var bottomStartCornerRadiusX = 0f
        var bottomStartCornerRadiusY = 0f
        if (corners and (CORNER_BOTTOM or CORNER_START) == CORNER_BOTTOM or CORNER_START) {
            bottomStartCornerRadiusX = rx
            bottomStartCornerRadiusY = ry
        }
        var bottomEndCornerRadiusX = 0f
        var bottomEndCornerRadiusY = 0f
        if (corners and (CORNER_BOTTOM or CORNER_END) == CORNER_BOTTOM or CORNER_END) {
            bottomEndCornerRadiusX = rx
            bottomEndCornerRadiusY = ry
        }
        val cornerDimensions = floatArrayOf(
            topStartCornerRadiusX, topStartCornerRadiusY,
            topEndCornerRadiusX, topEndCornerRadiusY,
            bottomStartCornerRadiusX, bottomStartCornerRadiusY,
            bottomEndCornerRadiusX, bottomEndCornerRadiusY
        )
        this.cornerDimensions?.run {
            if (this.size == cornerDimensions.size) {
                var changed = false
                for (index in this.indices) {
                    if (this[index] != cornerDimensions[index]) {
                        changed = true
                        break
                    }
                }

                if (!changed) {
                    return
                }
            }
        }

        this.cornerDimensions = cornerDimensions

        cornerRadiusRadiusX = rx
        cornerRadiusRadiusY = ry

        invalidate()
    }

    protected fun getCornerRadius(): Float {
        return if (cornerRadiusRadiusX < cornerRadiusRadiusY) {
            cornerRadiusRadiusY
        } else cornerRadiusRadiusX
    }
}
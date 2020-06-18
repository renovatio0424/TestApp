package com.herry.libs.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.herry.libs.R;


@SuppressWarnings("unused")
public class RoundedFrameLayout extends FrameLayout {
    private static final int DEFAULT_CORNER_RADIUS = 0;

    private Path baseClipPath;
    private float cornerRadiusRadiusX = DEFAULT_CORNER_RADIUS;
    private float cornerRadiusRadiusY = DEFAULT_CORNER_RADIUS;

    private static final int CORNER_TOP = 0x01;
    private static final int CORNER_BOTTOM = 0x02;
    private static final int CORNER_START = 0x04;
    private static final int CORNER_END = 0x08;
    private static final int CORNER_DEFAULT = CORNER_START | CORNER_END | CORNER_TOP | CORNER_BOTTOM;

    private float[] cornerDimensions = null;

    @NonNull
    private final Paint backgroundPaint = new Paint();

    public RoundedFrameLayout(Context context) {
        this(context, null);
    }

    public RoundedFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.RoundedFrameLayout);

        // set attributes
        if (null != attr) {
            float cornerRadius = attr.getDimensionPixelSize(R.styleable.RoundedFrameLayout_rfCornerRadius, 0);
            int corners = attr.getInt(R.styleable.RoundedFrameLayout_rfCorners, CORNER_DEFAULT);
            setCornerRadius(cornerRadius, corners);

            int backgroundColor = 0;
            if (attr.hasValue(R.styleable.RoundedFrameLayout_rfBackgroundColor)) {
                backgroundColor = attr.getColor(R.styleable.RoundedFrameLayout_rfBackgroundColor, 0);
            }
            backgroundPaint.setStyle(Paint.Style.FILL);
            backgroundPaint.setColor(backgroundColor);

            int strokeColor = attr.getColor(R.styleable.RoundedFrameLayout_rfStrokeColor, -1);
            int strokeWidth = attr.getDimensionPixelSize(R.styleable.RoundedFrameLayout_rfStrokeWidth, 0);

            attr.recycle();

            setForeground(createForegroundDrawable(strokeColor, strokeWidth));
        }
    }

    private Drawable createForegroundDrawable(int strokeColor, int strokeWidth) {
        GradientDrawable fgDrawable = new GradientDrawable();
        fgDrawable.setColor(Color.TRANSPARENT);
        fgDrawable.setCornerRadius(this.getCornerRadius());
        if (strokeColor != -1) {
            fgDrawable.setStroke(strokeWidth, strokeColor);
        }

        return fgDrawable;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (baseClipPath == null) {
            baseClipPath = new Path();
        }
        baseClipPath.reset();

        if (null != cornerDimensions) {
            baseClipPath.addRoundRect(
                    new RectF(
                            0,
                            0,
                            canvas.getWidth(),
                            canvas.getHeight()),
                    cornerDimensions,
                    Path.Direction.CW);
        } else {
            baseClipPath.addRoundRect(
                    new RectF(
                            0,
                            0,
                            canvas.getWidth(),
                            canvas.getHeight()),
                    cornerRadiusRadiusX,
                    cornerRadiusRadiusY,
                    Path.Direction.CW);
        }

        canvas.drawRoundRect(
                new RectF(
                        0,
                        0,
                        canvas.getWidth(),
                        canvas.getHeight()),
                cornerRadiusRadiusX, // rx
                cornerRadiusRadiusY, // ry
                backgroundPaint // Paint
        );

        canvas.clipPath(baseClipPath);

        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        requestLayout();
    }

    public void setCornerRadius(float radius) {
        setCornerRadius(radius, CORNER_DEFAULT);
    }

    public void setCornerRadius(float radius, int corners) {
        setCornerRadius(radius, radius, corners);
    }

    public void setCornerRadius(float rx, float ry, int corners) {
        float topStartCornerRadiusX = 0f;
        float topStartCornerRadiusY = 0f;

        if ((corners & (CORNER_TOP | CORNER_START)) == (CORNER_TOP | CORNER_START)) {
            topStartCornerRadiusX = rx;
            topStartCornerRadiusY = ry;
        }

        float topEndCornerRadiusX =  0f;
        float topEndCornerRadiusY = 0f;

        if ((corners & (CORNER_TOP | CORNER_END)) == (CORNER_TOP | CORNER_END)) {
            topEndCornerRadiusX = rx;
            topEndCornerRadiusY = ry;
        }

        float bottomStartCornerRadiusX = 0f;
        float bottomStartCornerRadiusY = 0f;
        if ((corners & (CORNER_BOTTOM | CORNER_START)) == (CORNER_BOTTOM | CORNER_START)) {
            bottomStartCornerRadiusX = rx;
            bottomStartCornerRadiusY = ry;
        }

        float bottomEndCornerRadiusX = 0f;
        float bottomEndCornerRadiusY = 0f;
        if ((corners & (CORNER_BOTTOM | CORNER_END)) == (CORNER_BOTTOM | CORNER_END)) {
            bottomEndCornerRadiusX = rx;
            bottomEndCornerRadiusY = ry;
        }

        float[] cornerDimensions = new float[] {
                topStartCornerRadiusX, topStartCornerRadiusY,
                topEndCornerRadiusX, topEndCornerRadiusY,
                bottomStartCornerRadiusX, bottomStartCornerRadiusY,
                bottomEndCornerRadiusX, bottomEndCornerRadiusY
        };

        if (null != this.cornerDimensions) {
            if (this.cornerDimensions.length == cornerDimensions.length) {
                boolean changed = false;
                for (int index = 0; index < this.cornerDimensions.length; index++) {
                    if (this.cornerDimensions[index] != cornerDimensions[index]) {
                        changed = true;
                        break;
                    }
                }

                if (!changed) {
                    return;
                }
            }
        }

        this.cornerDimensions = cornerDimensions;

        cornerRadiusRadiusX = rx;
        cornerRadiusRadiusY = ry;

        invalidate();
    }

    protected float getCornerRadius() {
        if (cornerRadiusRadiusX < cornerRadiusRadiusY) {
            return cornerRadiusRadiusY;
        }
        return cornerRadiusRadiusX;
    }
}
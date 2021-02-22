package com.herry.libs.widget.anim

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import java.util.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
class ViewAnimCreator(vararg views: View) {

    private val views: MutableList<View> = ArrayList()
    private val animators: MutableList<Animator> = ArrayList()

    var interpolator: Interpolator? = null
        private set
    var duration: Long = 3000
        private set
    var startDelay: Long = 0
        private set

    init {
        this.views.addAll(listOf(*views))
    }

    // sets object animator properties
    private fun property(property: String, vararg values: Float): ViewAnimCreator {
        for (view in views) {
            animators.add(ObjectAnimator.ofFloat(view, property, *values))
        }
        return this
    }

    /**
     * Translation x animation builder.
     * @param x x pixel value
     * @return ViewAnimCreator
     */
    fun translationX(vararg x: Float): ViewAnimCreator {
        return property("translationX", *x)
    }

    /**
     * Translation y animation builder.
     * @param y y pixel value
     * @return ViewAnimCreator
     */
    fun translationY(vararg y: Float): ViewAnimCreator {
        return property("translationY", *y)
    }

    /**
     * Alpha animation builder.
     * @param alpha 0.0f ~ 1.0f
     * @return ViewAnimCreator
     */
    fun alpha(vararg alpha: Float): ViewAnimCreator {
        return property("alpha", *alpha)
    }

    /**
     * Scale animation builder only x.
     * @param scale scale value
     * @return ViewAnimCreator
     */
    fun scaleX(vararg scale: Float): ViewAnimCreator {
        return property("scaleX", *scale)
    }

    /**
     * Scale animation builder only y.
     * @param scale scale value
     * @return ViewAnimCreator
     */
    fun scaleY(vararg scale: Float): ViewAnimCreator {
        return property("scaleY", *scale)
    }

    /**
     * Scale animation builder both x and y.
     * @param scale scale value
     * @return ViewAnimCreator
     */
    fun scale(vararg scale: Float): ViewAnimCreator {
        scaleX(*scale)
        scaleY(*scale)
        return this
    }

    fun pivotX(pivot: Float): ViewAnimCreator {
        for (view in views) {
            view.pivotX = pivot
        }
        return this
    }

    fun pivotY(pivot: Float): ViewAnimCreator {
        for (view in views) {
            view.pivotX = pivot
        }
        return this
    }

    fun rotationX(vararg rotation: Float): ViewAnimCreator {
        return property("rotationX", *rotation)
    }

    fun rotationY(vararg rotation: Float): ViewAnimCreator {
        return property("rotationY", *rotation)
    }

    fun rotation(vararg rotation: Float): ViewAnimCreator {
        return property("rotation", *rotation)
    }

    fun duration(duration: Long): ViewAnimCreator {
        this.duration = duration

//        for (Animator animator : animators) {
//            if (null == animator) {
//                continue;
//            }
//
//            animator.setDuration(this.duration);
//        }
        return this
    }

    fun startDelay(delay: Long): ViewAnimCreator {
        startDelay = delay
//        for (Animator animator : animators) {
//            if (null == animator) {
//                continue;
//            }
//
//            animator.setStartDelay(delay);
//        }
        return this
    }

    fun getAnimators(): MutableList<Animator> = animators

    /**
     * Interpolator view animator.
     *
     * @param interpolator the interpolator
     * @return the animation builder
     */
    fun interpolator(interpolator: Interpolator?): ViewAnimCreator {
        if (null == interpolator) {
            this.interpolator = AccelerateDecelerateInterpolator() // default
        } else {
            this.interpolator = interpolator
        }
        for (animator in animators) {
            animator.interpolator = this.interpolator
        }
        return this
    }
}
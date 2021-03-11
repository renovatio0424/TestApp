package com.herry.libs.widget.anim

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import java.util.*
import kotlin.collections.HashMap

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

    var keepEnd: Boolean = true
        private set

    private val reverseProperties: HashMap<String, FloatArray> = HashMap()

    private enum class PropertyKey(val value: String) {
        TRANSITION_X("translationX"),
        TRANSITION_Y("translationY"),
        ALPHA ("alpha"),
        SCALE_X ("scaleX"),
        SCALE_Y ("scaleY"),
        ROTATION_X ("rotationX"),
        ROTATION_Y ("rotationY"),
        ROTATION ("rotation"),
        PIVOT_X ("pivotX"),
        PIVOT_Y ("pivotY")
    }

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

    private fun reverseProperty(property: String, vararg values: Float) {
        if (property.isBlank()) {
            return
        }

        if (!keepEnd && values.isNotEmpty() && !reverseProperties.containsKey(property)) {
            reverseProperties[property] = values
        }
    }

    /**
     * Translation x animation builder.
     * @param x x pixel value
     * @return ViewAnimCreator
     */
    fun translationX(vararg x: Float): ViewAnimCreator {
        val key = PropertyKey.TRANSITION_X.value
        reverseProperty(key, *x)

        return property(key, *x)
    }

    /**
     * Translation y animation builder.
     * @param y y pixel value
     * @return ViewAnimCreator
     */
    fun translationY(vararg y: Float): ViewAnimCreator {
        val key = PropertyKey.TRANSITION_Y.value
        reverseProperty(key, *y)

        return property(key, *y)
    }

    /**
     * Alpha animation builder.
     * @param alpha 0.0f ~ 1.0f
     * @return ViewAnimCreator
     */
    fun alpha(vararg alpha: Float): ViewAnimCreator {
        val key = PropertyKey.ALPHA.value
        reverseProperty(key, *alpha)

        return property(key, *alpha)
    }

    /**
     * Scale animation builder only x.
     * @param scale scale value
     * @return ViewAnimCreator
     */
    fun scaleX(vararg scale: Float): ViewAnimCreator {
        val key = PropertyKey.SCALE_X.value
        reverseProperty(key, *scale)

        return property(key, *scale)
    }

    /**
     * Scale animation builder only y.
     * @param scale scale value
     * @return ViewAnimCreator
     */
    fun scaleY(vararg scale: Float): ViewAnimCreator {
        val key = PropertyKey.SCALE_Y.value
        reverseProperty(key, *scale)

        return property(key, *scale)
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
        reverseProperty(PropertyKey.PIVOT_X.value, pivot)

        for (view in views) {
            view.pivotX = pivot
        }
        return this
    }

    fun pivotY(pivot: Float): ViewAnimCreator {
        reverseProperty(PropertyKey.PIVOT_Y.value, pivot)

        for (view in views) {
            view.pivotX = pivot
        }
        return this
    }

    fun rotationX(vararg rotation: Float): ViewAnimCreator {
        val key = PropertyKey.ROTATION_X.value
        reverseProperty(key, *rotation)

        return property(key, *rotation)
    }

    fun rotationY(vararg rotation: Float): ViewAnimCreator {
        val key = PropertyKey.ROTATION_Y.value
        reverseProperty(key, *rotation)

        return property(key, *rotation)
    }

    fun rotation(vararg rotation: Float): ViewAnimCreator {
        val key = PropertyKey.ROTATION.value
        reverseProperty(key, *rotation)

        return property(key, *rotation)
    }

    fun animators(vararg animator: ObjectAnimator): ViewAnimCreator {
        animators.addAll(animator)

        return this
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

    fun keepEnd(keep: Boolean): ViewAnimCreator {
        this.keepEnd = keep

        return this
    }

    fun getAnimators(): MutableList<Animator> = animators

    fun getReverses(): MutableList<Animator> {
        val reverses: MutableList<Animator> = ArrayList()

        if (!keepEnd) {
            for (key in reverseProperties.keys) {
                for (view in views) {
                    val values = reverseProperties[key]
                    if (values != null && values.isNotEmpty()) {
                        reverses.add(ObjectAnimator.ofFloat(view, key, values[0]))
                    }
                }
            }
        }

        return reverses
    }

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
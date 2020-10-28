package com.herry.libs.app.activity_caller.module

import android.app.Activity
import android.graphics.Bitmap
import android.transition.TransitionInflater
import android.view.View
import android.widget.ImageView
import androidx.annotation.TransitionRes

class ACTransitionUtil(private val getActivity: () -> Activity?) {

    object BitmapStorage {
        private val map = mutableMapOf<String, Bitmap>()

        fun put(name: String, bitmap: Bitmap) {
            map[name] = bitmap
        }

        fun remove(name: String) {
            map.remove(name)
        }

        fun get(name: String): Bitmap? = map[name]

        fun clear() {
            map.clear()
        }
    }

    private val transitionViews = mutableListOf<View>()

    fun setImageView(name: String, transitionView: View, imageView: ImageView, @TransitionRes inflateTransition: Int = 0) {
        BitmapStorage.get(name)?.let {
            imageView.setImageBitmap(it)
            transitionView.transitionName = name

            if (0 != inflateTransition) {
                getActivity()?.let { activity ->
                    val transition = TransitionInflater.from(activity)
                        .inflateTransition(inflateTransition)
                    activity.window.enterTransition = transition
                    activity.window.exitTransition = transition
                }
            }
            BitmapStorage.remove(name)
        }
    }

    fun clearBitmapStorage() {
        BitmapStorage.clear()
    }

    fun finish(removeTransition: Boolean, enterTransition: Int = 0) {
        if(!removeTransition) {
            return
        }

        getActivity()?.let { activity ->
            var isTransition = false

            for(transitionView in transitionViews) {
                if(transitionView.transitionName != null) {
                    transitionView.transitionName = null
                    isTransition = true
                }
            }

            if(isTransition) {
                activity.window.sharedElementReturnTransition = null
                activity.window.sharedElementReenterTransition = null
                if (enterTransition == 0) {
                    activity.window.enterTransition = null
                } else {
                    activity.window.enterTransition =
                        TransitionInflater.from(activity).inflateTransition(enterTransition)
                }
                activity.window.exitTransition = null
            }
        }
    }
}
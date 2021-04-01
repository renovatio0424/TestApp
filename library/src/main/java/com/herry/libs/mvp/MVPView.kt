package com.herry.libs.mvp

import android.content.Context
import androidx.lifecycle.Lifecycle

@Suppress("unused")
interface MVPView<P> {
    var presenter: P?

    /**
     * Context
     */
    fun getViewContext(): Context?

    fun getLifecycle(): Lifecycle?

//    fun getViewLifecycleOwner(): LifecycleOwner?

    /**
     * Shows loading view
     */
    fun showViewLoading()

    /**
     * Hides loading view
     * @param success if success is true, it will be show "done" view and then hide it.
     */
    fun hideViewLoading(success: Boolean)

    fun error(throwable: Throwable)
}
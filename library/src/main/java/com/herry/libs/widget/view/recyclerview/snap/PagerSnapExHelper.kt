package com.herry.libs.widget.view.recyclerview.snap

import androidx.annotation.NonNull
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView


open class PagerSnapExHelper : PagerSnapHelper() {
    interface OnSnappedListener {
        fun onSnapped(position: Int, itemCount: Int)
        fun onUnsnapped(position: Int, itemCount: Int)
    }

    private var onSnappedListener: OnSnappedListener? = null

    private var snappedPosition = RecyclerView.NO_POSITION
    private var recyclerView: RecyclerView? = null

    open fun setOnSnappedListener(listener: OnSnappedListener?) {
        onSnappedListener = listener
    }

    private fun setSnappedPosition(newSnappedPosition: Int) {
        val recyclerView = this.recyclerView ?: return
        val adapter = recyclerView.adapter ?: return

        if (newSnappedPosition >= 0 && newSnappedPosition < adapter.itemCount) {
            var changed = false
            var unsnappedPosition = -1
            var snappedPosition = this.snappedPosition
            if (snappedPosition != newSnappedPosition) {
                unsnappedPosition = snappedPosition
                snappedPosition = newSnappedPosition
                changed = true
            }
            if (changed) {
                val itemCount: Int = adapter.itemCount
                this.snappedPosition = snappedPosition
                notifySnapped(unsnappedPosition, snappedPosition, itemCount)
            }
        }
    }

    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
        val targetSnapViewPosition = findTargetSnapViewPosition(layoutManager, velocityX, velocityY)

        setSnappedPosition(targetSnapViewPosition)

        return targetSnapViewPosition
    }

    protected fun findTargetSnapViewPosition(@NonNull layoutManager: RecyclerView.LayoutManager?, velocityX: Int, velocityY: Int): Int {
        return super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
    }

    private val onScrollListener = object: RecyclerView.OnScrollListener() {

        private var isScrolled = false
        private var currentPosition = RecyclerView.NO_POSITION

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE && isScrolled) {
                isScrolled = false

                if (currentPosition != RecyclerView.NO_POSITION) {
                    setSnappedPosition(currentPosition)
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val layoutManager = recyclerView.layoutManager ?: return

            if (dx != 0 || dy != 0) {
                isScrolled = true

                val view = findSnapView(layoutManager) ?: return
                val position = layoutManager.getPosition(view)

                if (currentPosition != position) {
                    currentPosition = position
                }
            }
        }
    }

    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        this.recyclerView = recyclerView?.also { _recyclerView ->
            _recyclerView.removeOnScrollListener(onScrollListener)
            _recyclerView.addOnScrollListener(onScrollListener)
        }
        super.attachToRecyclerView(this.recyclerView)
    }

    open fun scrollToSnapPosition(position: Int, force: Boolean = false) {
        val recyclerView = recyclerView ?: return
        val adapter = recyclerView.adapter ?: return

        val layoutManager: RecyclerView.LayoutManager? = recyclerView.layoutManager
        if (null != layoutManager && null != onSnappedListener && (position != getCurrentSnappedPosition() || force)) {
            val snapViewPosition = findTargetSnapViewPosition(layoutManager, recyclerView.computeHorizontalScrollOffset(), recyclerView.computeVerticalScrollOffset())
            if (snapViewPosition != position || force) {
                val itemCount: Int = adapter.itemCount
                layoutManager.scrollToPosition(position)

                var unsnappedPosition: Int = RecyclerView.NO_POSITION
                if (this.snappedPosition >= 0) {
                    unsnappedPosition = this.snappedPosition
                }
                this.snappedPosition = position
                notifySnapped(unsnappedPosition, this.snappedPosition, itemCount)
            }
        }
    }

    open fun getCurrentSnappedPosition(): Int = snappedPosition

    open fun getRecyclerView(): RecyclerView? = this.recyclerView

    protected open fun getOnSnappedListener(): OnSnappedListener? = onSnappedListener

    protected fun notifySnapped(unsnappedPosition: Int, snappedPosition: Int, itemCounts: Int) {
        val onSnappedListener = getOnSnappedListener() ?: return
        if (0 <= unsnappedPosition) {
            onSnappedListener.onUnsnapped(unsnappedPosition, itemCounts)
        }
        if (0 <= snappedPosition) {
            onSnappedListener.onSnapped(snappedPosition, itemCounts)
        }
    }

    open fun isScrollable(): Boolean {
        val adapter = recyclerView?.adapter ?: return false
        return  1 < adapter.itemCount
    }

    open fun snapToNext() {
        if (isScrollable()) {
            val recyclerView = recyclerView ?: return
            val adapter = recyclerView.adapter ?: return

            val currentSnappedPosition = this.snappedPosition
            if (RecyclerView.NO_POSITION == currentSnappedPosition || currentSnappedPosition >= adapter.itemCount - 1) {
                return
            }
            this.snappedPosition = currentSnappedPosition + 1
            recyclerView.smoothScrollToPosition(this.snappedPosition)
            notifySnapped(currentSnappedPosition, this.snappedPosition, adapter.itemCount)
        }
    }

    open fun snapToPrevious() {
        if (isScrollable()) {
            val recyclerView = recyclerView ?: return
            val adapter = recyclerView.adapter ?: return

            val currentSnappedPosition = this.snappedPosition
            if (RecyclerView.NO_POSITION == currentSnappedPosition || currentSnappedPosition <= 0) {
                return
            }
            this.snappedPosition = currentSnappedPosition - 1
            recyclerView.smoothScrollToPosition(this.snappedPosition)
            notifySnapped(currentSnappedPosition, this.snappedPosition, adapter.itemCount)
        }
    }
}
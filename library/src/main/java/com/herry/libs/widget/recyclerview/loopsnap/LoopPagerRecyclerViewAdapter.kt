package com.herry.libs.widget.recyclerview.loopsnap

import androidx.recyclerview.widget.RecyclerView
import java.util.*

@Suppress("WeakerAccess", "unused")
abstract class LoopPagerRecyclerViewAdapter<VH : RecyclerView.ViewHolder?, VM> : RecyclerView.Adapter<VH>() {
    private var maxItemCounts: Int = 0

    private val items: MutableList<VM> = ArrayList()

    private var defaultPosition: Int = RecyclerView.NO_POSITION

    fun setItems(items: Collection<VM>) {
        this.items.clear()
        this.items.addAll(items)

        notifyDataSetChanged()

        // sets default position
        val itemCounts = this.items.size
        if (1 < itemCounts) {
            maxItemCounts = Int.MAX_VALUE
            defaultPosition = maxItemCounts / 2 - maxItemCounts / 2 % this.items.size
        } else if (0 < itemCounts) {
            maxItemCounts = itemCounts
            defaultPosition = 0
        } else {
            maxItemCounts = this.items.size
        }
    }

    protected fun getItem(position: Int): VM? {
        val realPosition = getRealPosition(position)
        return if (0 <= realPosition && realPosition < items.size) {
            items[realPosition]
        } else null
    }

    override fun getItemCount(): Int = maxItemCounts

    open fun getRealItemCount(): Int = items.size

    open fun getRealPosition(fakePosition: Int): Int {
        return if (1 < items.size) fakePosition % items.size else fakePosition
    }

    open fun getFakePosition(realPosition: Int): Int {
        return if (0 <= realPosition && realPosition < items.size) {
            defaultPosition + realPosition
        } else RecyclerView.NO_POSITION
    }
}
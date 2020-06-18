package com.herry.libs.nodeview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

abstract class NodeView<H: NodeHolder> {

    open fun createHolder(context: Context, parent: ViewGroup?, attach: Boolean = true): H? {
        val view = onCreateView(context, parent)
        if(attach) {
            parent?.addView(view)
        }
        return bindHolder(context, view)
    }

    open fun bindHolder(context: Context, parent: ViewGroup?, @IdRes id: Int): H? {
        return bindHolder(context, parent?.findViewById(id))
    }

    open fun bindHolder(context: Context, view: View?): H? {
        return view?.let {
            onCreateHolder(context, it)
        }
    }

    protected open fun onCreateView(context: Context, parent: ViewGroup?): View =
            LayoutInflater.from(context).inflate(onLayout(), parent, false)

    @LayoutRes
    protected abstract fun onLayout(): Int

    protected abstract fun onCreateHolder(context: Context, view: View): H

}
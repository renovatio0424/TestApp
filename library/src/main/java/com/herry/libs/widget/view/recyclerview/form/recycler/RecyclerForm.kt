package com.herry.libs.widget.view.recyclerview.form.recycler

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.herry.libs.R
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.NodeView
import com.herry.libs.widget.view.viewgroup.LoadingCountView

@Suppress("unused")
abstract class RecyclerForm : NodeView<RecyclerForm.Holder>() {

    override fun onLayout(): Int = R.layout.recycler_form

    override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

    abstract fun onBindRecyclerView(context: Context, recyclerView: RecyclerView)

    fun scrollToPosition(position: Int, offset: Int? = null, smoothScroll: Boolean = false) {
        offset?.let {
            if(holder?.recyclerFormView?.layoutManager is LinearLayoutManager) {
                (holder?.recyclerFormView?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, it)
                return
            }
        }

        holder?.recyclerFormView?.run {
            if (smoothScroll) smoothScrollToPosition(position) else scrollToPosition(position)
        }
    }

    fun smoothScrollToPosition(position: Int) {
        holder?.recyclerFormView?.smoothScrollToPosition(position)
    }

    fun showLoading() {
        holder?.recyclerFormLoading?.show()
    }

    fun hideLoading() {
        holder?.recyclerFormLoading?.hide()
    }

    fun setEmptyView(view: View?) {
        if (view == null) {
            holder?.recyclerFormView?.visibility = View.VISIBLE
            holder?.recyclerFormEmptyView?.visibility = View.GONE
        } else {
            holder?.recyclerFormView?.visibility = View.INVISIBLE
            holder?.recyclerFormEmptyView?.apply {
                if (getChildAt(0) != view) {
                    removeAllViews()
                    addView(view)
                }
                visibility = View.VISIBLE
            }
        }
    }

    fun getEmptyParentView() = holder?.recyclerFormEmptyView

    fun setLoadView(view: View?) {
        if (view == null) {
            holder?.recyclerFormView?.visibility = View.VISIBLE
            holder?.recyclerFormLoadView?.visibility = View.INVISIBLE
        } else {
            holder?.recyclerFormEmptyView?.visibility = View.GONE
            holder?.recyclerFormView?.visibility = View.INVISIBLE
            holder?.recyclerFormLoadView?.apply {
                if (getChildAt(0) != view) {
                    removeAllViews()
                    addView(view)
                }
                visibility = View.VISIBLE
            }
        }
    }

    fun getLoadView() : View? {
        if (0 < holder?.recyclerFormLoadView?.childCount ?: 0) {
            return holder?.recyclerFormLoadView?.getChildAt(0)
        }
        return null
    }

    fun getLoadParentView() = holder?.recyclerFormLoadView

    fun setVisibility(visibility: Int) {
        holder?.view?.visibility = visibility
    }

    fun isVisible(isVisible: Boolean) {
        holder?.view?.isVisible = isVisible
    }

    inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
        val recyclerFormView: RecyclerView = view.findViewById(R.id.recycler_form_view)
        val recyclerFormEmptyView: ViewGroup = view.findViewById(R.id.recycler_form_empty)
        val recyclerFormLoadView: ViewGroup = view.findViewById(R.id.recycler_form_load_view)
        val recyclerFormLoading: LoadingCountView = view.findViewById(R.id.recycler_form_loading)
        init {
            onBindRecyclerView(context, recyclerFormView)
        }
    }
}
package com.herry.libs.widget.view.recyclerview.form

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.view.recyclerview.snap.LinearSnapExHelper
import kotlin.reflect.KClass

@Suppress("unused")
abstract class LinearSnapRecyclerViewForm<A : RecyclerView.Adapter<*>, T: Any>(mClass: KClass<T>)
    : NodeForm<LinearSnapRecyclerViewForm.Holder, T>(Holder::class, mClass)
{
    override fun onBindModel(context: Context, holder: Holder, model: T) {
        holder.recyclerView?.let { recycler ->
            if(recycler.adapter == null) {
                recycler.adapter = onGetAdapter(context)
            }
            @Suppress("UNCHECKED_CAST")
            onBindAdapter(context, recycler.adapter as A, recycler, model)
        }
    }

    protected abstract fun onGetAdapter(context: Context): A

    protected abstract fun onBindAdapter(context: Context, adapter: A, recyclerView: RecyclerView, item: T)

    class Holder(
        context: Context,
        view: View,
        internal val recyclerView: RecyclerView?,
        @DimenRes private val padding: Int,
        @ColorRes private val backgroundColor: Int
    ) : NodeHolder(context, view) {

        private val snapHelper = LinearSnapExHelper(LinearSnapExHelper.SnapStyle.START)

        init {
            recyclerView?.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
                itemAnimator = null
                clipToPadding = false

                val viewPadding = ViewUtil.getDimensionPixelSize(view.context, padding)
                view.setPadding(viewPadding, 0, viewPadding, 0)
                if (backgroundColor != 0) {
                    setBackgroundColor(ViewUtil.getColor(view.context, backgroundColor))
                }

                addOnScrollListener(
                    object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            recyclerView.parent.requestDisallowInterceptTouchEvent(true)
                        }
                    }
                )
                snapHelper.attachToRecyclerView(this)
            }
        }
    }
}
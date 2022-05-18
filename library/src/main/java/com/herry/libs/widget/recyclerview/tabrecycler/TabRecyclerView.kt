package com.herry.libs.widget.recyclerview.tabrecycler

import android.content.Context
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.herry.libs.R
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.model.NodeRoot
import com.herry.libs.nodeview.recycler.NodeRecyclerAdapter
import com.herry.libs.widget.recyclerview.endless.EndlessRecyclerViewScrollListener
import com.herry.libs.widget.recyclerview.form.recycler.RecyclerForm
import com.herry.libs.widget.view.LoadingCountView

@Suppress("unused")
open class TabRecyclerView(val listener: TabRecyclerViewListener?): NodeForm<TabRecyclerView.Holder, TabRecyclerContract.Presenter>(Holder::class, TabRecyclerContract.Presenter::class) {

    interface TabRecyclerViewListener {
        fun getCustomLayout(): Int = R.layout.recycler_form
        fun getCustomRecyclerForm(container: View): View? = null

        fun onBindRecyclerView(context: Context, recyclerView: RecyclerView, container: View)
        fun onBindHolder(list: MutableList<NodeForm<out NodeHolder, *>>)

        fun onScrollStateChanged(holder: Holder, recyclerView: RecyclerView, newState: Int)
        fun onScrolled(holder: Holder, recyclerView: RecyclerView, dx: Int, dy: Int)
        fun onScrollTop(holder: Holder)

        fun onError(throwable: Throwable)

        fun onBindEmptyView(context: Context, parent: ViewGroup?, visible: Boolean): View?
        fun onBindLoadView(context: Context, parent: ViewGroup?, visible: Boolean): View?
    }

    override fun onLayout(): Int {
        val layoutResID = listener?.getCustomLayout() ?: 0
        return if (0 != layoutResID) layoutResID else R.layout.recycler_form
    }

    override fun onCreateHolder(context: Context, view: View): Holder {
        return Holder(context, view)
    }

    override fun onBindModel(context: Context, holder: Holder, model: TabRecyclerContract.Presenter) {
        holder.presenter = model
    }

    inner class Holder(context: Context, view: View) : NodeHolder(context, view), TabRecyclerContract.View {
        override var presenter: TabRecyclerContract.Presenter? = null
            set(value) {
                field?.onDetach()
                field = value
                field?.onAttach(this)
            }

        override fun getViewContext(): Context = context

        override val root: NodeRoot
            get() = adapter.root

        private val adapter = Adapter { context }
        private var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null

        private val recyclerForm: RecyclerForm

        init {
            recyclerForm = object: RecyclerForm() {
                override fun onBindRecyclerView(context: Context, recyclerView: RecyclerView) {
                    recyclerView.apply {
                        listener?.onBindRecyclerView(context, this, view)

                        val scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
                            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                                super.onScrollStateChanged(recyclerView, newState)
                                listener?.onScrollStateChanged(this@Holder, recyclerView, newState)
                            }

                            override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
                                super.onScrolled(view, dx, dy)
                                listener?.onScrolled(this@Holder, view, dx, dy)
                            }

                            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                                presenter?.onLoadMore()
                            }
                        }
                        addOnScrollListener(scrollListener)
                        endlessRecyclerViewScrollListener = scrollListener

                        adapter = this@Holder.adapter
                    }
                }
            }.apply {
                bindHolder(context, listener?.getCustomRecyclerForm(view) ?: view.findViewById(R.id.recycler_form))
            }
        }

        override fun onAttached(saveInstanceState: Parcelable?) {
            view.findViewById<RecyclerView>(R.id.recycler_form_view)?.layoutManager?.onRestoreInstanceState(saveInstanceState)
        }

        override fun onDetached(): Parcelable? = view.findViewById<RecyclerView>(R.id.recycler_form_view)?.layoutManager?.onSaveInstanceState()

        override fun onNotifyScrollState() {
            listener?.onScrollStateChanged(this@Holder, view.findViewById(R.id.recycler_form_view), RecyclerView.SCROLL_STATE_IDLE)
        }

        override fun onEmptyView(visible: Boolean) {
            if (visible) endlessRecyclerViewScrollListener?.resetState()

            val context = getViewContext() ?: return
            val view = listener?.onBindEmptyView(context, recyclerForm.getEmptyParentView(), visible)

            recyclerForm.scrollToPosition(0)
            recyclerForm.setEmptyView(view)
        }

        override fun onLoadView(visible: Boolean) {
            val context = getViewContext() ?: return
            val view = listener?.onBindLoadView(context, recyclerForm.getLoadParentView(), visible)

            recyclerForm.scrollToPosition(0)
            recyclerForm.setLoadView(view)
        }

        override fun showViewLoading() {
            view.findViewById<LoadingCountView>(R.id.recycler_form_loading)?.show()
        }

        override fun hideViewLoading(success: Boolean) {
            view.findViewById<LoadingCountView>(R.id.recycler_form_loading)?.hide()
        }

        override fun error(throwable: Throwable) {
            listener?.onError(throwable)
        }

        private inner class Adapter(context: () -> Context) : NodeRecyclerAdapter(context) {
            override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
                listener?.onBindHolder(list)
            }
        }
    }
}
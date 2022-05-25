package com.herry.libs.widget.view.recyclerview.tabrecycler

import android.os.Parcelable
import com.herry.libs.mvp.MVPPresenter
import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot

interface TabRecyclerContract {

    interface View: MVPView<Presenter>, INodeRoot {
        fun onAttached(saveInstanceState: Parcelable?)
        fun onDetached(): Parcelable?

        fun onNotifyScrollState()

        fun onEmptyView(visible: Boolean)
        fun onLoadView(visible: Boolean)

        fun onScrollToPosition(position: Int)
    }

    abstract class Presenter: MVPPresenter<View>() {

        protected open fun onResume() {}

        abstract fun loadMore()
        abstract fun refresh(loading: TabRecyclerLoadingType)

        abstract fun setCurrentPresent()
        abstract fun scrollToPosition(position: Int)
    }
}
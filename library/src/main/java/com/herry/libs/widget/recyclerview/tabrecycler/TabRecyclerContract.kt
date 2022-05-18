package com.herry.libs.widget.recyclerview.tabrecycler

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
    }

    abstract class Presenter: MVPPresenter<View>() {
        abstract fun onLoadMore()
    }
}
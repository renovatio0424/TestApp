package com.herry.test.app.list.endless

import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.mvp.BasePresenter

interface EndlessListContract {
    interface View : MVPView<Presenter>, INodeRoot

    abstract class Presenter : BasePresenter<View>() {
        abstract fun loadMore()
    }

    data class ListItemData(
        val name: String
    )
}
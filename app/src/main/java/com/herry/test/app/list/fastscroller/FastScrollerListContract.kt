package com.herry.test.app.list.fastscroller

import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.mvp.BasePresent

interface FastScrollerListContract {
    interface View : MVPView<Presenter>, INodeRoot

    abstract class Presenter : BasePresent<View>()

    data class ListItemData(
        val name: String
    )
}
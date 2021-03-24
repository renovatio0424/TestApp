package com.herry.test.app.list.main

import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.mvp.BasePresent

interface ListContract {
    interface View : MVPView<Presenter>, INodeRoot

    abstract class Presenter : BasePresent<View>()

    enum class Type {
        FAST_SCROLLER,
        ENDLESS
    }
}
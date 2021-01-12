package com.herry.test.app.main

import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.mvp.BasePresent

/**
 * Created by herry.park on 2020/06/11.
 **/
interface MainContract {

    interface View : MVPView<Presenter>, INodeRoot {
        fun onScreen(type: TestItemType)
    }

    abstract class Presenter : BasePresent<View>() {
        abstract fun moveToScreen(type: TestItemType)
    }

    enum class TestItemType {
        SCHEME_TEST,
        GIF_DECODER,
        CHECKER_LIST,
        LAYOUT_SAMPLE,
        PICK,
        NESTED_FRAGMENTS
    }
}
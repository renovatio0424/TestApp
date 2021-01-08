package com.herry.test.app.nestedfragments.list

import com.herry.libs.mvp.IMvpView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.mvp.BasePresent

/**
 * Created by herry.park on 2020/06/11.
 **/
interface NestedFragmentsListContract {

    interface View : IMvpView<Presenter>, INodeRoot {
        fun onScreen(type: TestItemType)
    }

    abstract class Presenter : BasePresent<View>() {
        abstract fun moveToScreen(type: TestItemType)
    }

    enum class TestItemType {
        NORMAL,
        NAVIGATION
    }
}
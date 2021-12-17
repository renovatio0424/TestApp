package com.herry.test.app.intent.list

import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.mvp.BasePresenter

/**
 * Created by herry.park on 2020/06/11.
 **/
interface IntentListContract {

    interface View : MVPView<Presenter>, INodeRoot {
        fun onScreen(type: TestItemType)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun moveToScreen(type: TestItemType)
    }

    enum class TestItemType {
        SCHEME_TEST,
        MEDIA_SHARE_TEST
    }
}
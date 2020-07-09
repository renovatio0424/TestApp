package com.herry.test.app.intent.list

import com.herry.libs.mvp.IMvpView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.BasePresent
import com.herry.test.app.base.BaseView

/**
 * Created by herry.park on 2020/06/11.
 **/
interface IntentListContract {

    interface View : IMvpView<Presenter>, INodeRoot {
        fun onScreen(type: TestItemType)
    }

    abstract class Presenter : BasePresent<View>() {
        abstract fun moveToScreen(type: TestItemType)
    }

    enum class TestItemType {
        SCHEME_TEST,
        MEDIA_SHARE_TEST
    }
}
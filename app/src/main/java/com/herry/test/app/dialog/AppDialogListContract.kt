package com.herry.test.app.dialog

import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.mvp.BasePresent

/**
 * Created by herry.park on 2020/06/11.
 **/
interface AppDialogListContract {

    interface View : MVPView<Presenter>, INodeRoot {
        fun onScreen(type: TestItemType)
    }

    abstract class Presenter : BasePresent<View>() {
        abstract fun moveToScreen(type: TestItemType)
    }

    enum class TestItemType {
        TITLE_MESSAGE_BUTTON_1,
        TITLE_MESSAGE_BUTTON_2,
        TITLE_MESSAGE_BUTTON_3,
        TITLE_LIST_BUTTON_2,
        MESSAGE_BUTTON_3,
        TITLE_VIEW,
        VIEW,
        VIEW_BUTTON_1,
        CUSTOM_VIEW,
        RESIZE_DIALOG
    }
}
package com.herry.test.app.resizing

import androidx.annotation.DrawableRes
import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.mvp.BasePresenter

interface ResizingUIContract {
    interface View: MVPView<Presenter>, INodeRoot {
    }

    abstract class Presenter: BasePresenter<View>() {
    }

    data class MenuItemModel(@DrawableRes val icon: Int)
}
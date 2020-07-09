package com.herry.test.app.checker.list

import com.herry.libs.mvp.IMvpView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.BasePresent

/**
 * Created by herry.park on 2020/7/7
 **/
interface CheckerListContract {

    interface View : IMvpView<Presenter>, INodeRoot {
        fun onShow(item: ItemType)
    }

    abstract class Presenter : BasePresent<View>() {
        abstract fun show(item: ItemType)
    }

    enum class ItemType {
        CHANGE,
        MANDATORY,
        COMBINATION
    }
}
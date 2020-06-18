package com.herry.test.app.scheme

import com.herry.libs.mvp.IMvpView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.BasePresent

/**
 * Created by herry.park on 2020/06/11.
 **/
interface SchemeContract {

    interface View : IMvpView<Presenter>, INodeRoot {
        fun onGotoScheme(scheme: String)
    }

    abstract class Presenter : BasePresent<View>() {
        abstract fun gotoScheme(type: SchemeItemType)
    }

    enum class SchemeItemType(val url: String) {
        EFFECT ("kinemaster://kinemaster/kmasset/category/2"),
        OVERLAY_STICKER ("kinemaster://kinemaster/kmasset/category/1/subcategory/12"),
        OVERLAY_TEXT ("kinemaster://kinemaster/kmasset/category/1/subcategory/14"),
        TEXT_ARABIC ("kinemaster://kinemaster/kmasset/category/4/subcategory/7")
    }
}
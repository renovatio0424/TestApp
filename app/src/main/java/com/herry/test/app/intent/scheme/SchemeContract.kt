package com.herry.test.app.intent.scheme

import com.herry.libs.mvp.IMvpView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.mvp.BasePresent

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
        TEXT_ARABIC ("kinemaster://kinemaster/kmasset/category/4/subcategory/7"),
        ASSET_DYNAMIC_LINK("https://qf58u.app.goo.gl/FNwL"),
        KINEMASTER_DEEP_LINK("kinemaster://kinemaster"),
        KINEMASTER_DINAMIC_LINK("https://qf58u.app.goo.gl/oh4W"),


        PROJECT_FEED_HOME ("kinemaster://kinemaster/projectfeed"),
        PROJECT_FEED_HOME_DYNAMIC_LINK("https://qf58u.app.goo.gl/pmp7"),
        PROJECT_FEED_CATEGORY ("kinemaster://kinemaster/projectfeed/category/5fbcff7150ab1428a91ee16f"),
        PROJECT_FEED_CATEGORY_DYNAMIC_LINK("https://qf58u.app.goo.gl/cwEz"),
        PROJECT_FEED_SEARCH ("kinemaster://kinemaster/projectfeed/search/background"),
        PROJECT_FEED_SEARCH_DYNAMIC_LINK("https://qf58u.app.goo.gl/NJ4K"),
        PROJECT_FEED_DETAIL ("kinemaster://kinemaster/projectfeed/5fbf2fc430c58627044b3f0b"),
        PROJECT_FEED_DETAIL_DYNAMIC_LINK("https://qf58u.app.goo.gl/gBMK"),
    }
}
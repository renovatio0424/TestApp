package com.herry.test.app.intent.scheme

import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.mvp.BasePresenter

/**
 * Created by herry.park on 2020/06/11.
 **/
interface SchemeContract {

    interface View : MVPView<Presenter>, INodeRoot {
        fun onGotoScheme(scheme: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun gotoScheme(type: SchemeItemType)
        abstract fun gotoScheme(url: String)
    }

    enum class SchemeItemType(val url: String) {
        EFFECT ("kinemaster://kinemaster/kmasset/category/2"),
        OVERLAY_STICKER ("kinemaster://kinemaster/kmasset/category/1/subcategory/12"),
        OVERLAY_TEXT ("kinemaster://kinemaster/kmasset/category/1/subcategory/14"),
        TEXT_ARABIC ("kinemaster://kinemaster/kmasset/category/4/subcategory/7"),
        ASSET_DYNAMIC_LINK("https://qf58u.app.goo.gl/FNwL"),
        KINEMASTER_DEEP_LINK("kinemaster://kinemaster"),
//        KINEMASTER_DINAMIC_LINK("https://qf58u.app.goo.gl/oh4W"),
        KINEMASTER_DINAMIC_LINK("https://qf58u.app.goo.gl/fTeQ"),

        KINEMASTER_NEW_PROJECT ("kinemaster://kinemaster/kmproject"),
        PROJECT_FEED_HOME ("kinemaster://kinemaster/projectfeed"),
        PROJECT_FEED_HOME_DYNAMIC_LINK("https://qf58u.app.goo.gl/pmp7"),
        PROJECT_FEED_CATEGORY ("kinemaster://kinemaster/projectfeed/category/5fbcff7150ab1428a91ee16f"),
        PROJECT_FEED_CATEGORY_DYNAMIC_LINK("https://qf58u.app.goo.gl/cwEz"),
        PROJECT_FEED_SEARCH ("kinemaster://kinemaster/projectfeed/search/background"),
        PROJECT_FEED_SEARCH_DYNAMIC_LINK("https://qf58u.app.goo.gl/NJ4K"),
        PROJECT_FEED_DETAIL ("kinemaster://kinemaster/projectfeed/5ffef0a09b485d21f62915e7"),
        PROJECT_FEED_DETAIL_DYNAMIC_LINK("https://qf58u.app.goo.gl/gBMK"),
    }

    data class SchemaData (
        val title: String,
        val appLink: String,
        val dynamicLink: String,
    )
}
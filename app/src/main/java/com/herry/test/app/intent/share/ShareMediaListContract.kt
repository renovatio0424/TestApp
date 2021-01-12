package com.herry.test.app.intent.share

import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.mvp.BasePresent
import com.herry.test.data.MediaFileInfoData

/**
 * Created by herry.park on 2020/06/11.
 **/
interface ShareMediaListContract {

    interface View : MVPView<Presenter>, INodeRoot {
        fun onShare(content: MediaFileInfoData)
    }

    abstract class Presenter : BasePresent<View>() {
        abstract fun share(content: MediaFileInfoData)
    }

}
package com.herry.test.app.intent.share

import com.herry.libs.mvp.IMvpView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.BasePresent
import com.herry.test.data.MediaFileInfoData

/**
 * Created by herry.park on 2020/06/11.
 **/
interface ShareMediaListContract {

    interface View : IMvpView<Presenter>, INodeRoot {
        fun onShare(content: MediaFileInfoData)
    }

    abstract class Presenter : BasePresent<View>() {
        abstract fun share(content: MediaFileInfoData)
    }

}
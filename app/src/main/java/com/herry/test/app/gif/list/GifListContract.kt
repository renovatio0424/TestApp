package com.herry.test.app.gif.list

import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.mvp.BasePresent
import com.herry.test.data.GifMediaFileInfoData

/**
 * Created by herry.park on 2020/06/11.
 **/
interface GifListContract {

    interface View : MVPView<Presenter>, INodeRoot {
        fun onDetail(content: GifMediaFileInfoData)
    }

    abstract class Presenter : BasePresent<View>() {
        abstract fun decode(content: GifMediaFileInfoData)
    }

}
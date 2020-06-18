package com.herry.test.app.gif.list

import com.herry.libs.mvp.IMvpView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.BasePresent
import com.herry.test.data.GifFileInfoData

/**
 * Created by herry.park on 2020/06/11.
 **/
interface GifListContract {

    interface View : IMvpView<Presenter>, INodeRoot {
        fun onDetail(content: GifFileInfoData)
    }

    abstract class Presenter : BasePresent<View>() {
        abstract fun decode(content: GifFileInfoData)
    }

}
package com.herry.test.app.intent.share

import com.herry.libs.mvp.MVPView
import com.herry.libs.nodeview.INodeRoot
import com.herry.test.app.base.mvp.BasePresent

/**
 * Created by herry.park on 2020/06/11.
 **/
interface ShareMediaListContract {

    interface View : MVPView<Presenter>, INodeRoot

    abstract class Presenter : BasePresent<View>()

}
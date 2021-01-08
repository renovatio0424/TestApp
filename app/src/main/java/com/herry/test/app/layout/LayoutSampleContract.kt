package com.herry.test.app.layout

import com.herry.libs.mvp.IMvpView
import com.herry.test.app.base.mvp.BasePresent

/**
 * Created by herry.park on 2020/08/19.
 **/
interface LayoutSampleContract {

    interface View : IMvpView<Presenter>

    abstract class Presenter : BasePresent<View>()

    enum class AspectRatioType {
        RATIO_16v9,
        RATIO_9v16,
        RATIO_1v1,
        RATIO_4v3,
        RATIO_3v4,
        RATIO_4v5,
        RATIO_2_35v1
    }
}
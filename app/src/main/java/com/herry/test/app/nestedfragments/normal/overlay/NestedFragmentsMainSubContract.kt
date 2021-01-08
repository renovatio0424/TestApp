package com.herry.test.app.nestedfragments.normal.overlay

import com.herry.libs.mvp.IMvpView
import com.herry.test.app.base.mvp.BasePresent

interface NestedFragmentsMainSubContract {

    interface View: IMvpView<Presenter> {
        fun onLaunched(name: String)
    }

    abstract class Presenter: BasePresent<View>() {

    }
}
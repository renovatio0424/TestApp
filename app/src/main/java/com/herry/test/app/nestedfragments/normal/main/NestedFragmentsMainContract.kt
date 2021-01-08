package com.herry.test.app.nestedfragments.normal.main

import com.herry.libs.mvp.IMvpView
import com.herry.test.app.base.mvp.BasePresent

interface NestedFragmentsMainContract {

    interface View: IMvpView<Presenter> {

    }

    abstract class Presenter: BasePresent<View>() {
    }
}
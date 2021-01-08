package com.herry.test.app.nestedfragments.normal.second

import com.herry.libs.mvp.IMvpView
import com.herry.test.app.base.mvp.BasePresent

interface NestedFragmentsSecondContract {
    interface View: IMvpView<Presenter> {

    }

    abstract class Presenter: BasePresent<View>() {
    }
}
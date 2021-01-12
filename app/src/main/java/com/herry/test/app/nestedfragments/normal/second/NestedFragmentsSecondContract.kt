package com.herry.test.app.nestedfragments.normal.second

import com.herry.libs.mvp.MVPView
import com.herry.test.app.base.mvp.BasePresent

interface NestedFragmentsSecondContract {
    interface View: MVPView<Presenter> {

    }

    abstract class Presenter: BasePresent<View>() {
    }
}
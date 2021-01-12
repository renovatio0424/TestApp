package com.herry.test.app.nestedfragments.normal.main

import com.herry.libs.mvp.MVPView
import com.herry.test.app.base.mvp.BasePresent

interface NestedFragmentsMainContract {

    interface View: MVPView<Presenter> {

    }

    abstract class Presenter: BasePresent<View>() {
    }
}
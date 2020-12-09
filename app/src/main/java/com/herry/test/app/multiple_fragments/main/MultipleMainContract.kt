package com.herry.test.app.multiple_fragments.main

import com.herry.libs.mvp.IMvpView
import com.herry.test.app.base.BasePresent

interface MultipleMainContract {

    interface View: IMvpView<Presenter> {

    }

    abstract class Presenter: BasePresent<View>() {
    }
}
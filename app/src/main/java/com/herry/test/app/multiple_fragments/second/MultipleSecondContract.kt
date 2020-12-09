package com.herry.test.app.multiple_fragments.second

import com.herry.libs.mvp.IMvpView
import com.herry.test.app.base.BasePresent

interface MultipleSecondContract {
    interface View: IMvpView<Presenter> {

    }

    abstract class Presenter: BasePresent<View>() {
    }
}
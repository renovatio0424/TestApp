package com.herry.test.app.multiple_fragments.overlay

import com.herry.libs.mvp.IMvpView
import com.herry.test.app.base.BasePresent

interface SubOverlayContract {

    interface View: IMvpView<Presenter> {
        fun onLaunched(name: String)
    }

    abstract class Presenter: BasePresent<View>() {

    }
}
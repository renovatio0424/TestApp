package com.herry.test.app.multiple_fragments.overlay

class SubOverlayPresenter(val name: String?): SubOverlayContract.Presenter() {
    override fun onLaunched(view: SubOverlayContract.View) {
        view.onLaunched(name ?: "")
    }
}
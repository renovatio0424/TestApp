package com.herry.test.app.nestedfragments.normal.overlay

class NestedFragmentsMainSubPresenter(val name: String?): NestedFragmentsMainSubContract.Presenter() {
    override fun onLaunch(view: NestedFragmentsMainSubContract.View, recreated: Boolean) {
        view.onLaunched(name ?: "")
    }
}
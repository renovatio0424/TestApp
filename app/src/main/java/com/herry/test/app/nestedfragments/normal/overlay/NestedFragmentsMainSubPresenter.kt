package com.herry.test.app.nestedfragments.normal.overlay

class NestedFragmentsMainSubPresenter(val name: String?): NestedFragmentsMainSubContract.Presenter() {
    override fun onLaunched(view: NestedFragmentsMainSubContract.View) {
        view.onLaunched(name ?: "")
    }
}
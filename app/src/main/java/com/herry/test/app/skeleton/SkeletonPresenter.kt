package com.herry.test.app.skeleton

class SkeletonPresenter : SkeletonContract.Presenter() {

    override fun onLaunch(view: SkeletonContract.View, recreated: Boolean) {
        show()
    }

    override fun show() {
        view?.onUpdate(SkeletonContract.ContentsModel(true))
    }

    override fun hide() {
        view?.onUpdate(SkeletonContract.ContentsModel(false))
    }
}
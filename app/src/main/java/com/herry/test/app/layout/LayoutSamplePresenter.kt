package com.herry.test.app.layout

/**
 * Created by herry.park on 2020/08/19.
 **/
class LayoutSamplePresenter : LayoutSampleContract.Presenter() {

    private var selectedRatio: LayoutSampleContract.AspectRatioType? = null

    override fun onLaunch(view: LayoutSampleContract.View, recreated: Boolean) {
        displayRatios(selectedRatio)
    }

    override fun onResume(view: LayoutSampleContract.View) {
        super.onResume(view)
    }

    override fun selectRatio(type: LayoutSampleContract.AspectRatioType?) {
        selectedRatio = type
        displayRatios(selectedRatio)
    }

    private fun displayRatios(type: LayoutSampleContract.AspectRatioType?) {
        view?.getContext() ?: return

        view?.onUpdateRatios(type)
    }
}
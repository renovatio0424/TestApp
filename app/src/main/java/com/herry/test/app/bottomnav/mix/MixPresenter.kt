package com.herry.test.app.bottomnav.mix

class MixPresenter : MixContract.Presenter() {

    private var counts: Int = 1

    override fun onLaunch(view: MixContract.View, recreated: Boolean) {
        launch {
            if (!recreated) {
                counts = 1
            }
            displayCounts(counts++)
        }
    }

    override fun onResume(view: MixContract.View) {
        launch {
            displayCounts(counts++)
        }
    }

    private fun displayCounts(counts: Int) {
        view?.onUpdateCounts(counts)
    }
}
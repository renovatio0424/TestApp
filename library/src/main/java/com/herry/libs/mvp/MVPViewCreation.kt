package com.herry.libs.mvp

interface MVPViewCreation<V: MVPView<P>, P: MVPPresenter<V>> {
    fun onCreatePresenter(): P?

    fun onCreatePresenterView(): V
}
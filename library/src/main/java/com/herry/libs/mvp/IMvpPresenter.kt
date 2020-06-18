package com.herry.libs.mvp

@Suppress("unused")
interface IMvpPresenter<in V> {
    fun onAttach(view: V)

    fun onDetach()

    fun onLaunch()

    fun onPause()
}
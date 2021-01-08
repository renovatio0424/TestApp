package com.herry.libs.mvp

@Suppress("unused")
interface OnPresenter<in V> {
    fun onAttach(view: V)

    fun onDetach()

    fun onLaunch()

    fun onPause()
}
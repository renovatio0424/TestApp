package com.herry.test.app.base

import com.herry.libs.mvp.IMvpPresenter

abstract class BasePresent<V>: IMvpPresenter<V> {

    protected var view: V? = null
        private set

    private var launched = false

    override fun onAttach(view: V) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun onLaunch() {
        this.view?.let {
            if(!launched) {
                launched = true
                onLaunched(it)
            } else {
                onResume(it)
            }
        }
    }

    override fun onPause() {
    }

    abstract fun onLaunched(view: V)

    protected open fun onResume(view: V) {}

    protected open fun launched(function: () -> Unit) {
        if (view != null) {
            function.invoke()
        }
    }
}
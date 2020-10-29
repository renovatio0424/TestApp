package com.herry.test.app.base.nav

import android.content.Context
import android.os.Bundle
import com.herry.libs.mvp.IMvpPresenter
import com.herry.libs.mvp.IMvpView
import com.herry.libs.app.activity_caller.module.ACError

@Suppress("unused")
abstract class NavView<V: IMvpView<P>, P: IMvpPresenter<V>>: NavFragment(), IMvpView<P> {
    override var presenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.presenter = this.presenter ?: onCreatePresenter()
        this.presenter?.onAttach(onCreatePresenterView()) ?: finishAndResults(null)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter?.onDetach()
    }

    override fun onResume() {
        super.onResume()

        presenter?.onLaunch()
    }

    override fun onPause() {
        presenter?.onPause()

        super.onPause()
    }

    override fun getViewContext(): Context? = context

    override fun error(throwable: Throwable) {
        activityCaller?.call(
            ACError.Caller(throwable) {
                onError(it)
            }
        )
    }

    open fun onError(throwable: Throwable) {

    }

    abstract fun onCreatePresenter(): P?

    abstract fun onCreatePresenterView(): V

    override fun onTransitionStart() {
        super.onTransitionStart()

        if(presenter is NavPresent<*>) {
            (presenter as NavPresent<*>).navTransitionStart()
        }
    }

    override fun onTransitionEnd() {
        super.onTransitionEnd()

        if(presenter is NavPresent<*>) {
            (presenter as NavPresent<*>).navTransitionEnd()
        }
    }
}
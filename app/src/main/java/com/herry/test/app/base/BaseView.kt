package com.herry.test.app.base

import android.content.Context
import android.os.Bundle
import com.herry.libs.mvp.IMvpPresenter
import com.herry.libs.mvp.IMvpView
import com.herry.test.app.base.activity_caller.module.ACError

abstract class BaseView<V: IMvpView<P>, P: IMvpPresenter<V>>: BaseFragment(), IMvpView<P> {
    override var presenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.presenter = this.presenter ?: onCreatePresenter()
        this.presenter?.onAttach(onCreatePresenterView()) ?: activity?.finishAfterTransition()
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
        aC?.call(
            ACError.Caller(throwable) {
                onError(it)
            }
        )
    }

    open fun onError(throwable: Throwable) {

    }

    abstract fun onCreatePresenter(): P?

    abstract fun onCreatePresenterView(): V

    override fun showViewLoading() {
        // implements show loading view to base fragment
    }

    override fun hideViewLoading(success: Boolean) {
        // implements hide loading view to base fragment
    }
}
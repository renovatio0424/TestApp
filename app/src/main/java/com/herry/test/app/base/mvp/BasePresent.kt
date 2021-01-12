package com.herry.test.app.base.mvp

import com.herry.libs.mvp.MVPPresenter
import com.herry.libs.mvp.MVPView
import com.herry.test.rx.RxSchedulerProvider
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

abstract class BasePresent<V> : MVPPresenter<V>() {

    protected var view: V? = null
        private set

    protected val compositeDisposable = CompositeDisposable()

    private var launched = false

    private var reloaded = false

    override fun onAttach(view: V) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null

        compositeDisposable.dispose()
    }

    override fun reloaded(reloaded: Boolean) {
        this.reloaded = reloaded
    }

    override fun onLaunch() {
        this.view?.let {
            if (!launched) {
                launched = true
                onLaunched(it)
            } else if (reloaded) {
                reloaded = false
                onReloaded(it)
            } else {
                onResume(it)
            }
        }
    }

    override fun onPause() {
        compositeDisposable.clear()
    }

    abstract fun onLaunched(view: V)

    protected open fun onReloaded(view: V) {}

    protected open fun onResume(view: V) {}

    protected open fun launched(function: () -> Unit) {
        if (view != null) {
            function.invoke()
        }
    }

    protected fun <T> subscribeObservable(
        observable: Observable<T>,
        onNext: ((T) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
        subscribeOn: Scheduler = RxSchedulerProvider.io(),
        observerOn: Scheduler = RxSchedulerProvider.ui(),
        loadView: Boolean = true
    ) {

        val subscribeObservable = observable
            .subscribeOn(subscribeOn)
            .observeOn(observerOn)
            .doOnSubscribe {
                if (loadView) {
                    (view as? MVPView<*>)?.showViewLoading()
                }
            }
            .doOnError {
                (view as? MVPView<*>)?.error(it)
                if (loadView) {
                    (view as? MVPView<*>)?.hideViewLoading(false)
                }
            }
            .doOnComplete {
                if (loadView) {
                    (view as? MVPView<*>)?.hideViewLoading(true)
                }
            }
            .doOnDispose {
                if (loadView) {
                    (view as? MVPView<*>)?.hideViewLoading(true)
                }
            }

        compositeDisposable.add(
            subscribeObservable.subscribe({
                onNext?.let { next -> next(it) }
            }, {
                onError?.let { error -> error(it) }
            }, {
                onComplete?.let { it() }
            })
        )
    }
}
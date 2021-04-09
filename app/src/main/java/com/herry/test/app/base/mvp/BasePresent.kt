package com.herry.test.app.base.mvp

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.coroutineScope
import com.herry.libs.mvp.MVPPresenter
import com.herry.libs.mvp.MVPView
import com.herry.test.rx.RxSchedulerProvider
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

abstract class BasePresent<V> : MVPPresenter<V>() {

    protected var view: V? = null
        private set

    private var _compositeDisposable: CompositeDisposable? = null

    // This property is only valid between onAttach and onDetach.
    protected val compositeDisposable get() = _compositeDisposable!!

    private var launched = false

    private var reloaded = false

    override fun onAttach(view: V) {
        this.view = view

        if (_compositeDisposable == null || _compositeDisposable?.isDisposed == true) {
            if (_compositeDisposable != null) {
                _compositeDisposable = null
            }
            _compositeDisposable = CompositeDisposable()
        }
    }

    override fun onDetach() {
        this.view = null

        compositeDisposable.dispose()
    }

    override fun reloaded(reloaded: Boolean) {
        this.reloaded = reloaded
    }

    final override fun onLaunch() {
        this.view?.let {
            if (!launched) {
                launched = true
                onLaunch(it, false)
            } else if (reloaded) {
                reloaded = false
                onLaunch(it, true)
            } else {
                onResume(it)
            }
        }
    }

    final override fun onPause() {
        compositeDisposable.clear()
        this.view?.let {
            onPause(it)
        }
    }

    protected abstract fun onLaunch(view: V, recreated: Boolean = false)

    protected open fun onResume(view: V) {}

    protected open fun onPause(view: V) {}

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

    protected fun lifecycleScope(): LifecycleCoroutineScope? = (view as? MVPView<*>)?.getViewLifecycleOwner()?.lifecycle?.coroutineScope
}
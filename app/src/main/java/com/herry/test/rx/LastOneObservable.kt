package com.herry.test.rx

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class LastOneObservable<T>(
    private var onNext: ((next: T) -> Unit)? = null,
    private var onError: ((error: Throwable) -> Unit)? = null,
    private var onComplete: (() -> Unit)? = null,
    private val onlyLastOne: Boolean = true
) {

    var disposable: Disposable? = null
    private var nextObservable: Observable<T>? = null

    fun subscribeOnNext(onNext: ((next: T) -> Unit)?) {
        this.onNext = onNext
    }

    fun subscribeOnError(onError: ((error: Throwable) -> Unit)?) {
        this.onError = onError
    }

    fun subscribeOnComplete(onComplete: (() -> Unit)?) {
        this.onComplete = onComplete
    }

    fun subscribe(function: () -> Observable<T>) {
        subscribe(function())
    }

    fun subscribe(observable: Observable<T>) {
        if(disposable?.isDisposed == false) {
            nextObservable = observable
            return
        }

        disposable = observable.subscribe(
            { next ->
                if(onlyLastOne) {
                    if (nextObservable == null) {
                        onNext?.let { it(next) }
                    }
                } else {
                    onNext?.let { it(next) }
                }
            } ,
            { error ->
                onError?.let { it(error) }
            },
            {
                nextObservable?.let {
                    nextObservable = null
                    subscribe(it)
                } ?: onComplete?.let { it() }
            }
        )
    }

    fun dispose() {
        nextObservable = null
        disposable?.dispose()
    }

    fun isDisposed() = disposable == null || disposable?.isDisposed == true
}
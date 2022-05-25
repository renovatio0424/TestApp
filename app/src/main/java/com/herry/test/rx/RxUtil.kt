package com.herry.test.rx

import com.herry.libs.mvp.MVPView
import io.reactivex.Observable
import io.reactivex.Scheduler

@Suppress("unused")
object RxUtil{

    fun <T> setPresenterObservable(
        observable: Observable<T>,
        view: () -> MVPView<*>?,
        subscribeOn: Scheduler = RxSchedulerProvider.io(),
        observerOn: Scheduler = RxSchedulerProvider.ui(),
        loadView: Boolean = true): Observable<T> {

        return observable
            .subscribeOn(subscribeOn)
            .observeOn(observerOn)
            .doOnSubscribe {
                if(loadView) {
                    view()?.showViewLoading()
                }
            }
            .doOnError {
                view()?.error(it)
                if(loadView) {
                    view()?.hideViewLoading(false)
                }
            }
            .doOnComplete {
                if(loadView) {
                    view()?.hideViewLoading(true)
                }
            }
            .doOnDispose {
                if(loadView) {
                    view()?.hideViewLoading(true)
                }
            }
    }
}
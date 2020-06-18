package com.herry.test.rx

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by herry.park on 2020/06/18.
 **/
object RxSchedulerProvider {

    fun computation(): Scheduler = Schedulers.computation()

    fun io(): Scheduler = Schedulers.io()

    fun new(): Scheduler = Schedulers.newThread()

    fun ui(): Scheduler = AndroidSchedulers.mainThread()
}
package com.herry.libs.app.activity_caller.module

import android.content.Intent
import com.herry.libs.app.activity_caller.ACModule
import com.herry.libs.helper.PopupHelper
import kotlin.reflect.KClass

class ACInject(private val listener: ACModule.OnListener<ACInject>): ACModule {

    private var popupHelper = PopupHelper {
        listener.getActivity()
    }

    class Caller<T: Any> (
        internal val cls: KClass<T>,
        internal val inject: ((inject: T) -> Unit)
    )

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return false
    }

    override fun call() {
    }

    fun <T: Any> call(caller: Caller<T>) {
        call(caller.cls).let {
            caller.inject(it)
        }
    }

    fun <T: Any> call(cls: KClass<T>): T {
        return when (cls) {
            PopupHelper::class -> {
                @Suppress("UNCHECKED_CAST")
                popupHelper as T
            }
            else -> {
                throw IllegalArgumentException("not implemented ACInject $cls")
            }
        }
    }
}
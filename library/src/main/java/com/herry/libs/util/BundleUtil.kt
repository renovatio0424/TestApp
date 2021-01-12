package com.herry.libs.util

import android.os.Bundle
import com.herry.libs.app.nav.NavMovement
import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

/**
 * Created by herry.park on 2020/06/18.
 **/
@Suppress("unused")
object BundleUtil {
    fun <T> getSerializableData(bundle: Bundle?, key: String?, tClass: Class<T>?): T? {
        if (bundle != null && key != null && tClass != null) {
            val serializable = bundle.getSerializable(key)
            if (tClass.isInstance(serializable)) {
                return tClass.cast(serializable)
            }
        }
        return null
    }

    fun <T> getSerializableData(data: Serializable?, tClass: Class<T>?): T? {
        if (data != null && tClass != null) {
            if (tClass.isInstance(data)) {
                return tClass.cast(data)
            }
        }
        return null
    }

    fun <T: Any> getSerializableData(bundle: Bundle?, key: String?, kClass: KClass<T>?): T? {
        if (bundle != null && key != null && kClass != null) {
            val serializable = bundle.getSerializable(key)
            if (kClass.isInstance(serializable)) {
                return kClass.cast(serializable)
            }
        }
        return null
    }

    fun <T: Any> getSerializableData(data: Serializable?, kClass: KClass<T>?): T? {
        if (data != null && kClass != null) {
            if (kClass.isInstance(data)) {
                return kClass.cast(data)
            }
        }
        return null
    }

    fun <T: Any> getSerializableDataDefault(data: Serializable?, default: T): T {
        if (data != null) {
            if (default::class.isInstance(data)) {
                return default::class.cast(data)
            }
        }
        return default
    }

    fun createNavigationBundle(resultOk: Boolean): Bundle {
        val bundle = Bundle()
        bundle.putBoolean(NavMovement.NAV_UP_RESULT_OK, resultOk)
        return bundle
    }

    fun isNavigationResultOk(bundle: Bundle?): Boolean {
        return bundle != null && bundle.getBoolean(NavMovement.NAV_UP_RESULT_OK, false)
    }

    fun fromNavigationId(bundle: Bundle?): Int {
        return bundle?.getInt(NavMovement.NAV_UP_FROM_ID, 0) ?: 0
    }

    operator fun <T> get(bundle: Bundle?, key: String, tClass: Class<T>): T? {
        if (null != bundle) {
            val obj = bundle.get(key)
            if (tClass.isInstance(obj)) {

                @Suppress("UNCHECKED_CAST")
                return obj as T
            }
        }

        return null
    }

    operator fun <T: Any> get(bundle: Bundle?, key: String, kClass: KClass<T>): T? {
        if (null != bundle) {
            val obj = bundle.get(key)
            if (kClass.isInstance(obj)) {
                return kClass.cast(obj)
            }
        }

        return null
    }

    operator fun <T: Any> get(bundle: Bundle?, key: String, default: T): T {
        if (null != bundle) {
            val obj = bundle.get(key)
            if (default::class.isInstance(obj)) {
                return default::class.cast(obj)
            }
        }

        return default
    }
}
package com.herry.libs.util.preferences

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object PreferenceUtil {
    private var PREFERENCE_NAME: String = ""

    private var context: () -> Context? = { null }

    fun init(context: () -> Context?, name: String = "") {
        this.context = context
        this.PREFERENCE_NAME = if (name.isEmpty()) {
            this.context.invoke()?.packageName.plus(".pref")
        } else {
            name
        }
    }

    private fun getSharedPreferences(): SharedPreferences? {
        return context()?.run {
            getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun getSharedPreferencesEditor(): SharedPreferences.Editor? {
        return getSharedPreferences()?.run { edit() }
    }

    fun clear() {
        getSharedPreferencesEditor()?.let {
            it.clear()
            it.commit()
        }
    }

    fun clear(key: PreferenceKey) {
        getSharedPreferencesEditor()?.let {
            it.remove(key.value)
            it.commit()
        }
    }

    fun resetVolatile() {
        getSharedPreferencesEditor()?.let {
            for(value in PreferenceKey.values()) {
                if(value.volatile) {
                    it.remove(value.toString())
                }
            }
            it.commit()
        }
    }

    fun <T> set(key: PreferenceKey, value: T) {
        getSharedPreferencesEditor()?.let {
            when (value) {
                is Boolean -> it.putBoolean(key.value, value).commit()
                is Int -> it.putInt(key.value, value).commit()
                is Long -> it.putLong(key.value, value).commit()
                is Float -> it.putFloat(key.value, value).commit()
                is String -> it.putString(key.value, value).commit()
                is JSONObject, is JSONArray -> {
                    it.putString(key.value, value.toString()).commit()
                }
                else -> {
                }
            }
        }
    }

    fun <T> get(key: PreferenceKey, value: T): T {
        getSharedPreferences()?.let {
            @Suppress("UNCHECKED_CAST")
            return when (value) {
                is Boolean -> it.getBoolean(key.value, value) as T
                is Int -> it.getInt(key.value, value) as T
                is Long -> it.getLong(key.value, value) as T
                is Float -> it.getFloat(key.value, value) as T
                is String -> it.getString(key.value, value) as T
                is JSONObject -> {
                    try {
                        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                        JSONObject(it.getString(key.value, value.toString())) as T
                    } catch (e: JSONException) {
                        value
                    }
                }
                is JSONArray -> {
                    try {
                        JSONArray(it.getString(key.value, value.toString())) as T
                    } catch (e: JSONException) {
                        value
                    }
                }
                else -> value
            }
        }
        return value
    }
}
package com.herry.libs.util.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 *
 * Shared Preferences helper
 *
 * Usage:
 *
 *  1. initializes PreferenceHelper() in the Application
 *      ex>
 *      onCreate() {
 *          ...
 *          // initialize preference helper
 *          PreferenceHelper.init { applicationContext }
 *          ...
 *      }
 *
 *  2. Defines shared preference keys to "PreferenceKey.kt"
 *
 *  3. If you wanna new shared preference name, add to "PreferenceName.kt"
 *
 *  4. Gets shared preference
 *      PreferenceHelper.get(PreferenceKey, default value)
 *      - PreferenceKey: preference key which is defined at PreferenceKey
 *      - default value: If preference key is not exit at shared preference, default value.
 *                      It is MUST same class type with saved shared preference
 *
 *      ex> PreferenceHelper.get(PreferenceKey.NOTICE_TIME, 0L)
 *
 *  5. Sets shared preference
 *      PreferenceHelper.set(PreferenceKey, value)
 *
 *      5.1. Single sets
 *          ex> PreferenceHelper.set(PreferenceKey.NOTICE_TIME, noticeTimeStamp)
 *
 *      5.2. Complex sets
 *          Uses #PrefSetData
 *          ex>
 *          in kotlin:
 *              PreferenceHelper.applies(
 *                  PrefSetData(PreferenceKey, value), // put
 *                  PrefSetData(PreferenceKey, value), // put
 *                  PrefClearData(PreferenceKey), // remove
 *              )
 *          in java:
 *              PreferenceHelper.applies(Arrays.asList(
 *                  new PrefSetData(PreferenceKey, value), // put
 *                  new PrefSetData(PreferenceKey, value), // put
 *                  new PrefClearData(PreferenceKey), // remove
 *              ));
 *      5.3. Complex sets
 *          Sets and removes
 *
 *  6. Also you can get SharedPreference with PreferenceName using getSharedPreferences(PreferenceName).
 *     ex>
 *     PreferenceHelper.getSharedPreferences(PreferenceName.DEFAULT)?.run {
 *          this.edit()?.let { editor ->
 *              // puts "test1" to "set1"
 *              editor.put(PreferenceKey.TEST, "set1")
 *              // puts "test2" to "set2"
 *              editor.put(PreferenceKey.TEST, "set2")
 *              // removes "test3"
 *              editor.remove(PreferenceKey.TEST)
 *              editor.apply()
 *          }
 *     }
 *
 *     // gets saved "set1" value of "test1"
 *     PreferenceHelper.get(PreferenceKey.TEST.setPlusKey("1"), "default")
 *     // gets saved "set2" value of "test2"
 *     PreferenceHelper.get(PreferenceKey.TEST.setPlusKey("2"), "default")
 *     // gets "default" value of "test3"
 *     PreferenceHelper.get(PreferenceKey.TEST.setPlusKey("3"), "default")
 *
 * <notice>
 *  When set/get shared preference, data is MUST equal class type.
 * */
/*
    <TEST Codes>

    Log.d("PrefHelperTest", "PreferenceHelper.gets ----------------------------------")
    Log.d("PrefHelperTest", "test_1 ${PreferenceHelper.get(PreferenceKey.TEST_1, "default")}")
    Log.d("PrefHelperTest", "test_2 ${PreferenceHelper.get(PreferenceKey.TEST_2, "default")}")

    PreferenceHelper.set(PreferenceKey.TEST_1, "set-1")
    PreferenceHelper.set(PreferenceKey.TEST_2, "set-1")

    Log.d("PrefHelperTest", "PreferenceHelper.set ----------------------------------")
    Log.d("PrefHelperTest", "test_1 ${PreferenceHelper.get(PreferenceKey.TEST_1, "default")}")
    Log.d("PrefHelperTest", "test_2 ${PreferenceHelper.get(PreferenceKey.TEST_2, "default")}")

    Log.d("PrefHelperTest", "PreferenceHelper.applies(set,clear) ------------------------------")
    PreferenceHelper.applies(
            PrefSetData(PreferenceKey.TEST_1, "set-2"), // put
            PrefClearData(PreferenceKey.TEST_2), // remove
    )
    Log.d("PrefHelperTest", "test_1 ${PreferenceHelper.get(PreferenceKey.TEST_1, "default")}")
    Log.d("PrefHelperTest", "test_2 ${PreferenceHelper.get(PreferenceKey.TEST_2, "default")}")

    Log.d("PrefHelperTest", "PreferenceHelper.reset ----------------------------------")
    PreferenceHelper.reset(PreferenceName.TEST)
    Log.d("PrefHelperTest", "test_1 ${PreferenceHelper.get(PreferenceKey.TEST_1, "default")}")
    Log.d("PrefHelperTest", "test_2 ${PreferenceHelper.get(PreferenceKey.TEST_2, "default")}")

    Log.d("PrefHelperTest", "PreferenceHelper.clear ----------------------------------")
    PreferenceHelper.clear(PreferenceName.TEST)
    Log.d("PrefHelperTest", "test_1 ${PreferenceHelper.get(PreferenceKey.TEST_1, "default")}")
    Log.d("PrefHelperTest", "test_2 ${PreferenceHelper.get(PreferenceKey.TEST_2, "default")}")

    Log.d("PrefHelperTest", "PreferenceHelper.get(default) ----------------------------------")
    Log.d("PrefHelperTest", "test_1 ${try {
        PreferenceHelper.get(PreferenceKey.TEST_1)
    } catch (ex: IllegalArgumentException) {
        ex.message
    }}")
    Log.d("PrefHelperTest", "test_2 ${try {
        PreferenceHelper.get(PreferenceKey.TEST_2)
    } catch (ex: IllegalArgumentException) {
        ex.message
    }}")
    Log.d("PrefHelperTest", "test_3 ${try {
        val getValue = PreferenceHelper.get(PreferenceKey.TEST_DEFAULT_LONG)
        if (getValue is Long) {
            "long $getValue"
        } else {
            "type converting error"
        }
    } catch (ex: IllegalArgumentException) {
        ex.message
    }}")
 */
@Suppress("unused")
object PreferenceHelper {
    private var context: () -> Context? = { null }

    fun init(context: () -> Context?) {
        PreferenceHelper.context = context
    }
    
    fun getSharedPreferences(prefName: PreferenceName): SharedPreferences? {
        return getSharedPreferences(prefName.prefName)
    }
    
    private fun getSharedPreferences(prefName: String = PreferenceNameDefault.PreferenceName): SharedPreferences? {
        val context = context.invoke()
        return if ((prefName.isBlank() || prefName == PreferenceNameDefault.PreferenceName) && context != null) {
            PreferenceManager.getDefaultSharedPreferences(context)
        } else {
            context?.run {
                getSharedPreferences(prefName, Context.MODE_PRIVATE)
            }
        }
    }

    private fun getSharedPreferencesEditor(prefKey: PreferenceKey): SharedPreferences.Editor? {
        return getSharedPreferencesEditor(prefKey.prefName)
    }

    private fun getSharedPreferencesEditor(prefName: PreferenceName): SharedPreferences.Editor? {
        return getSharedPreferences(prefName.prefName)?.run { edit() }
    }
    
    fun clear(prefName: PreferenceName) {
        getSharedPreferencesEditor(prefName)?.let { editor ->
            editor.clear()
            editor.apply()
        }
    }
    
    fun clear(key: PreferenceKey) {
        getSharedPreferencesEditor(key.prefName)?.let { editor ->
            editor.remove(key.key)
            editor.apply()
        }
    }

    private fun reset(prefName: PreferenceName, keys: MutableList<PreferenceKey>) {
        getSharedPreferencesEditor(prefName)?.let { editor ->
            keys.filter { key -> key.volatile && key.prefName == prefName }.forEach { key ->
                editor.remove(key.key)
            }
            editor.apply()
        }
    }

    private fun <T> setInternal(editor: SharedPreferences.Editor, key: PreferenceKey, value: T): SharedPreferences.Editor {
        when (value) {
            is Boolean -> editor.putBoolean(key.key, value)
            is Int -> editor.putInt(key.key, value)
            is Long -> editor.putLong(key.key, value)
            is Float -> editor.putFloat(key.key, value)
            is String -> editor.putString(key.key, value)
            is JSONObject, is JSONArray -> {
                editor.putString(key.key, value.toString())
            }
            is Set<*> -> {
                val setString = mutableSetOf<String>()
                value.filterIsInstance<String>().forEach { v ->
                    setString.add(v)
                }
                editor.putStringSet(key.key, setString)
            }
            else -> {
            }
        }
        return editor
    }

    /**
     * Removes key from shared preference, after this function is called, you MUST commit
     */
    
    fun remove(editor: SharedPreferences.Editor?, key: PreferenceKey) {
        editor?.let {
            editor.remove(key.key)
        }
    }
    
    fun <T> put(editor: SharedPreferences.Editor?, key: PreferenceKey, value: T): SharedPreferences.Editor? {
        editor?.let {
            setInternal(it, key, value)
        }

        return editor
    }

    /**
     * after this function is called, editor is MUST committed call side
     */
    @JvmName("put_kt")
    fun <T> SharedPreferences.Editor.put(key: PreferenceKey, value: T): SharedPreferences.Editor {
        setInternal(this, key, value)
        return this
    }
    
    fun <T> set(key: PreferenceKey, value: T) {
        getSharedPreferencesEditor(key)?.put(key, value)?.apply()
    }
    
    fun <T> setCommit(key: PreferenceKey, value: T) {
        getSharedPreferencesEditor(key)?.put(key, value)?.commit()
    }

    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key The [PreferenceKey] of the preference to retrieve. It MUST defined defaultValue.
     *
     * @return Returns the preference value if it exists, or defaultValue of PreferenceKey. Throws
     * IllegalArgumentException if a defaultValue of PreferenceKey is not set..
     *
     */
    @Throws(IllegalArgumentException::class)
    fun get(key: PreferenceKey): Any {
        val defaultValue = key.defaultValue ?: throw IllegalArgumentException("MUST define default value to PreferenceKey")
        return get(key, defaultValue)
    }

    /**
     * Retrieve a value from the preferences.
     *
     * @param key The PreferenceKey of the preference to retrieve.
     * @param defaultValue Value to return if this preference does not exist.
     *
     * @return Returns the preference value if it exists, or defValue.
     */
    fun <T> get(key: PreferenceKey, defaultValue: T): T {
        getSharedPreferences(key.prefName)?.let {
            @Suppress("UNCHECKED_CAST")
            return when (defaultValue) {
                is Boolean -> it.getBoolean(key.key, defaultValue) as T
                is Int -> it.getInt(key.key, defaultValue) as T
                is Long -> it.getLong(key.key, defaultValue) as T
                is Float -> it.getFloat(key.key, defaultValue) as T
                is String -> it.getString(key.key, defaultValue) as T
                is JSONObject -> {
                    try {
                        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                        JSONObject(it.getString(key.key, defaultValue.toString())) as T
                    } catch (e: JSONException) {
                        defaultValue
                    }
                }
                is JSONArray -> {
                    try {
                        JSONArray(it.getString(key.key, defaultValue.toString())) as T
                    } catch (e: JSONException) {
                        defaultValue
                    }
                }
                is Set<*> -> {
                    try {
                        it.getStringSet(key.key, defaultValue as Set<String>) as T
                    } catch (e: ClassCastException) {
                        defaultValue
                    }
                }
                else -> defaultValue
            }
        }
        return defaultValue
    }

    /**
     * Checks whether the preferences contains a preference.
     *
     * @param key The [PreferenceKey] of the preference to check.
     * @return Returns true if the preference exists in the preferences,
     *         otherwise false.
     */
    fun contains(key: PreferenceKey): Boolean {
        return getSharedPreferences(key.prefName)?.contains(key.key) ?: false
    }

    fun applies(sets: MutableList<PreferenceData>) {
        applies(*sets.toTypedArray())
    }

    fun applies(vararg sets: PreferenceData) {
        val editorNames = mutableMapOf<PreferenceName, MutableList<PreferenceData>>()
        sets.forEach {
            val prefName = it.prefKey.prefName
            val prefKeys = editorNames[prefName] ?: mutableListOf()
            prefKeys.add(it)

            editorNames[prefName] = prefKeys
        }

        editorNames.keys.forEach { prefName ->
            getSharedPreferencesEditor(prefName)?.let { editor ->
                editorNames[prefName]?.forEach { value ->
                    when (value) {
                        is PreferenceSetData -> {
                            editor.put(value.prefKey, value.value)
                        }
                        is PreferenceClearData -> {
                            editor.remove(value.prefKey.key)
                        }
                    }

                }
                editor.apply()
            }
        }
    }
}

abstract class PreferenceData(val prefKey: PreferenceKey)

data class PreferenceSetData(
    private val key: PreferenceKey,
    val value: Any
) : PreferenceData(key)

data class PreferenceClearData(
    private val key: PreferenceKey
) : PreferenceData(key)
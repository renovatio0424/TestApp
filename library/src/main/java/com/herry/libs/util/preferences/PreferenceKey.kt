package com.herry.libs.util.preferences

import java.io.Serializable

/**
 * Shared Preference Key
 *
 * @param prefName Shared Preference Storage Name
 *               @see com.herry.libs.util.preferences.PreferenceName
 * @param key The main key name of the preference
 * @param volatile If it is false, the [key] is not clear on PrefHelper.reset()
 * @param defaultValue Value to return if this preference does not exist.
 */
data class PreferenceKey(
    val prefName: PreferenceName = PreferenceNameDefault(),
    val key: String,
    val volatile: Boolean,
    val defaultValue: Any? = null
) : Serializable

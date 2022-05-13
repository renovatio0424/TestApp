package com.herry.libs.util.preferences

/**
 * Shared Preference Storage Default Name
 */
class PreferenceNameDefault: PreferenceName() {
    companion object {
        const val PreferenceName =  "default.pref"
    }
    override val prefName: String = PreferenceName
}
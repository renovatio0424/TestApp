package com.herry.test.app.bottomnav.helper

enum class NavScreenActions(val key: String) {
    SHOW_SETTINGS("SHOW_SETTINGS");

    companion object {
        fun generate(key: String) : NavScreenActions? = values().firstOrNull { it.key == key }
    }

}
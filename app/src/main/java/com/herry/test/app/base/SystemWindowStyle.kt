package com.herry.test.app.base

data class ScreenWindowStyle(
    val isFullScreen: Boolean = false,
    val statusBarStyle: StatusBarStyle? = null
)

enum class StatusBarStyle {
    LIGHT,
    DARK
}
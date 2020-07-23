package com.herry.libs.util.preferences

import java.io.Serializable

data class PreferenceKey(
    val value: String,
    val volatile: Boolean
) : Serializable

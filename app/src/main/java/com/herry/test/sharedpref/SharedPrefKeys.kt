package com.herry.test.sharedpref

import com.herry.libs.util.preferences.PreferenceKey
import com.herry.libs.util.preferences.PreferenceNameDefault

/**
 * Created by herry.park on 2020/07/22.
 **/
object SharedPrefKeys {
    val PASSWORD = PreferenceKey(prefName = PreferenceNameDefault(),
        key = "PASSWORD", volatile = true)

}
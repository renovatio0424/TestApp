package com.herry.test.app.bottomnav

import android.os.Bundle
import com.herry.libs.util.ViewUtil
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavActivity

class BottomNavActivity : BaseNavActivity() {
    override fun getGraph(): Int = R.navigation.bottom_nav_main_navigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ViewUtil.makeFullScreen(this/*, ViewUtil.getColor(this, R.color.tbc_10_a10)*/)
    }
}
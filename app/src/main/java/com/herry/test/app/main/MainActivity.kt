package com.herry.test.app.main

import com.herry.test.R
import com.herry.test.app.base.nav.NavActivity

class MainActivity : NavActivity() {

    override fun getGraph(): Int = R.navigation.main_navigation
}
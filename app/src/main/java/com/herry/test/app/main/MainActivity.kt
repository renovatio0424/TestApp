package com.herry.test.app.main

import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavActivity

class MainActivity : BaseNavActivity() {

    override fun getGraph(): Int = R.navigation.main_navigation
}
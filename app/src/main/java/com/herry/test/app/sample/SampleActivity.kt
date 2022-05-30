package com.herry.test.app.sample

import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavActivity

class SampleActivity : BaseNavActivity() {
    override fun getGraph(): Int = R.navigation.sample_navigation
}
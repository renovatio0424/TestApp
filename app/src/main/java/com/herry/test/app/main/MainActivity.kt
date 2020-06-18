package com.herry.test.app.main

import androidx.fragment.app.Fragment
import com.herry.test.app.base.SingleActivity

class MainActivity : SingleActivity() {

    override fun getBaseFragment(): Fragment? = MainFragment()
}
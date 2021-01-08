package com.herry.test.app.nestedfragments.normal

import androidx.fragment.app.Fragment
import com.herry.test.R
import com.herry.test.app.base.nested.BaseNestedActivity
import com.herry.test.app.nestedfragments.normal.main.NestedFragmentsMainFragment

class NestedFragmentsActivity: BaseNestedActivity() {

    override fun getHostViewID() = R.id.multiple_fragment_activity_screen_container

    override fun getContentViewID() = R.layout.multiple_fragment_activity

    override fun getStartFragment(): Fragment = NestedFragmentsMainFragment.newInstance()
}
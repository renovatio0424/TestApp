package com.herry.test.app.multiple_fragments

import androidx.fragment.app.Fragment
import com.herry.test.R
import com.herry.test.app.base.nested.NestedActivity
import com.herry.test.app.multiple_fragments.main.MultipleMainFragment

class MultipleFragmentsActivity: NestedActivity() {

    override fun getHostViewID() = R.id.multiple_fragment_activity_screen_container

    override fun getContentViewID() = R.layout.multiple_fragment_activity

    override fun getStartFragment(): Fragment = MultipleMainFragment.newInstance()
}
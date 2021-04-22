package com.herry.test.app.nestedfragments.nav

import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavActivity

class NestedNavFragmentsActivity: BaseNavActivity() {
    override fun getGraph() = R.navigation.nested_nav_fragments_navigation
}
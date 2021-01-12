package com.herry.test.app.nestedfragments.nav

import com.herry.test.R
import com.herry.test.app.base.nestednav.BaseNestedNavActivity

class NestedNavFragmentsActivity: BaseNestedNavActivity() {
    override fun getGraph() = R.navigation.nested_nav_fragments_navigation
}
//class NestedNavFragmentsActivity: AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.nested_nav_fragments_activity)
//    }
//
//    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.nested_nav_fragments_activity_nav_host_fragment).navigateUp()
//}
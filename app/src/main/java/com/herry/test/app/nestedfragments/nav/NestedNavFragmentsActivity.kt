package com.herry.test.app.nestedfragments.nav

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.herry.test.R

//class NestedNavFragmentsActivity: BaseNavActivity() {
//    override fun getGraph() = R.navigation.nested_nav_fragments_navigation
//}
class NestedNavFragmentsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nested_nav_fragments_activity)
    }

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.nested_nav_fragments_activity_nav_host_fragment).navigateUp()
}
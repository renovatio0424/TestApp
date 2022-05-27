package com.herry.test.app.nbnf.screen1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.extension.navigateTo
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavFragment
import com.herry.test.app.nbnf.screen2.NBNFScreen2Fragment

class NBNFScreen1Child1Fragment : BaseNavFragment() {

    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.nbnf_screen_1_child_1_fragment, container, false)
            init(this.container)
        } else {
            // fixed: "java.lang.IllegalStateException: The specified child already has a parent.
            // You must call removeView() on the child's parent first."
            ViewUtil.removeViewFormParent(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return

        view.findViewById<Button>(R.id.nbnf_screen_1_child_1_fragment_go_screen_2)?.setOnClickListener {
            navigateTo(destinationId = R.id.nbnf_screen_2_fragment, args = Bundle().apply {
                putString(NBNFScreen2Fragment.ARG_FROM_TEXT, "child1")
            })
        }
    }
}
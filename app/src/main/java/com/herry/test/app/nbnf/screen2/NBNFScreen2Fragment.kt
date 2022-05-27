package com.herry.test.app.nbnf.screen2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.herry.libs.app.nav.NavBundleUtil
import com.herry.libs.util.BundleUtil
import com.herry.libs.util.ViewUtil
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavFragment

class NBNFScreen2Fragment : BaseNavFragment() {

    companion object {
        const val ARG_FROM_TEXT = "ARG_FROM_TEXT"
        const val RESULT_MESSAGE = "RESULT_FROM_TEXT"
    }
    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.nbnf_screen_2_fragment, container, false)
            init(this.container)
        } else {
            // fixed: "java.lang.IllegalStateException: The specified child already has a parent.
            // You must call removeView() on the child's parent first."
            ViewUtil.removeViewFormParent(this.container)
        }
        return this.container
    }

    @SuppressLint("SetTextI18n")
    private fun init(view: View?) {
        view ?: return

        view.findViewById<TextView>(R.id.nbnf_screen_2_fragment_from)?.let { textView ->
            val fromString = BundleUtil[arguments, ARG_FROM_TEXT, ""]
            if (fromString.isNotEmpty()) {
                textView.text = "from $fromString"
            }
        }
    }

    override fun getNavigateUpResult(): Bundle {
        return NavBundleUtil.createNavigationBundle(true, Bundle().apply {
            putString(RESULT_MESSAGE, BundleUtil[arguments, ARG_FROM_TEXT, ""])
        })
    }
}
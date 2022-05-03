package com.herry.test.app.bottomnav.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavFragment
import com.herry.test.app.bottomnav.helper.NavScreenActionHelper

class CreateFragment: BaseNavFragment() {

    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.create_fragment, container, false)
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

        view.findViewById<View>(R.id.create_fragment_setting)?.apply {
            setOnProtectClickListener {
                NavScreenActionHelper.showSetting(this@CreateFragment)
            }
        }
    }
}
package com.herry.test.app.nestedfragments.nav.overlay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavFragment
import com.herry.test.app.base.nestednav.BaseNestedNavFragment

class NestedNavFragmentsOverlayBaseFragment : BaseNavFragment() {

    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.nested_nav_fragments_base_overlay_fragment, container, false)
        }

        return this.container
    }

    override fun isSkipNavigateUp(): Boolean = true
}
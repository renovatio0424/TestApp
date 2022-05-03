package com.herry.test.app.bottomnav.mix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.herry.libs.util.ViewUtil
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavView

class MixFragment: BaseNavView<MixContract.View, MixContract.Presenter>(), MixContract.View {

    override fun onCreatePresenter(): MixContract.Presenter = MixPresenter()

    override fun onCreatePresenterView(): MixContract.View = this

    private var container: View? = null

    private var countText: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.mix_fragment, container, false)
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

        countText = view.findViewById(R.id.mix_fragment_counts)
    }

    override fun onUpdateCounts(counts: Int) {
        countText?.text = counts.toString()
    }
}
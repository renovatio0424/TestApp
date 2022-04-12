package com.herry.test.app.bottomnav.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.libs.util.ViewUtil
import com.herry.test.R
import com.herry.test.app.base.mvp.BaseView

class DiscoverFragment: BaseView<DiscoverContract.View, DiscoverContract.Presenter>(), DiscoverContract.View {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment
     */
    companion object {
        fun newInstance()  = DiscoverFragment().apply{
            arguments = Bundle()
        }
    }

    override val enterTransition: Int
        get() = android.R.transition.fade

    override val exitTransition: Int
        get() = 0

    override fun onCreatePresenter(): DiscoverContract.Presenter = DiscoverPresenter()

    override fun onCreatePresenterView(): DiscoverContract.View = this

    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.discover_fragment, container, false)
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
    }
}
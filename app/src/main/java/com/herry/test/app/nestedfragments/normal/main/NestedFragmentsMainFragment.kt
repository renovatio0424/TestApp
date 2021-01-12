package com.herry.test.app.nestedfragments.normal.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.libs.util.BundleUtil
import com.herry.libs.util.FragmentAddingOption
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.test.R
import com.herry.test.app.base.nested.BaseNestedView
import com.herry.test.app.nestedfragments.normal.overlay.NestedFragmentsMainSubFragment
import com.herry.test.app.nestedfragments.normal.second.NestedFragmentsSecondFragment


class NestedFragmentsMainFragment : BaseNestedView<NestedFragmentsMainContract.View, NestedFragmentsMainContract.Presenter>(), NestedFragmentsMainContract.View {

    companion object {
        fun newInstance(): NestedFragmentsMainFragment = NestedFragmentsMainFragment().apply {
            val args = Bundle()
            this.arguments = args
        }
    }

    private var container: View? = null

    override fun onCreatePresenter(): NestedFragmentsMainContract.Presenter = NestedFragmentsMainPresenter()

    override fun onCreatePresenterView(): NestedFragmentsMainContract.View = this

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.nested_fragment_main_fragment, container, false)
            init(this.container)
        }
        return this.container
    }

    private var currentSubIndex = 2
    private fun init(view: View?) {
        view ?: return

        view.findViewById<View>(R.id.nested_fragment_main_fragment_top_call_overlay)?.setOnProtectClickListener {
            addChildFragment(
                R.id.nested_fragment_main_fragment_overlay_container,
                NestedFragmentsMainSubFragment.newInstance("1"),
                FragmentAddingOption(isReplace = false, isAddToBackStack = true)
            ) { requestKey, bundle ->
                val isOk = BundleUtil.isNavigationResultOk(bundle)
                Log.d("Herry", "requestKey= $requestKey, isOk = $isOk")
            }
        }

        view.findViewById<View>(R.id.nested_fragment_main_fragment_call_sub)?.setOnProtectClickListener {
            addChildFragment(
                R.id.nested_fragment_main_fragment_sub1_container,
                NestedFragmentsMainSubFragment.newInstance((currentSubIndex++).toString()),
                FragmentAddingOption(isReplace = false, isAddToBackStack = true)
            ) { requestKey, bundle ->
                val isOk = BundleUtil.isNavigationResultOk(bundle)
                Log.d("Herry", "requestKey= $requestKey, isOk = $isOk")
            }
        }

        view.findViewById<View>(R.id.nested_fragment_main_fragment_bottom_second)?.setOnProtectClickListener {
            this@NestedFragmentsMainFragment.view?.rootView?.parent
            addFragmentToActivity(
                NestedFragmentsSecondFragment.newInstance()
            ) { requestKey, bundle ->
                val isOk = BundleUtil.isNavigationResultOk(bundle)
                Log.d("Herry", "requestKey= $requestKey, isOk = $isOk")
            }
        }
    }

    override fun onBackPressed(): Boolean {
        finishAndResults(true)
        return true
    }
}
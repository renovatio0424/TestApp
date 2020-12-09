package com.herry.test.app.multiple_fragments.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.libs.util.BundleUtil
import com.herry.libs.util.FragmentAddingOption
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.test.R
import com.herry.test.app.base.nested.NestedView
import com.herry.test.app.multiple_fragments.overlay.SubOverlayFragment
import com.herry.test.app.multiple_fragments.second.MultipleSecondFragment


class MultipleMainFragment : NestedView<MultipleMainContract.View, MultipleMainContract.Presenter>(), MultipleMainContract.View {

    companion object {
        fun newInstance(): MultipleMainFragment = MultipleMainFragment().apply {
            val args = Bundle()
            this.arguments = args
        }
    }

    private var container: View? = null

    override fun onCreatePresenter(): MultipleMainContract.Presenter = MultipleMainPresenter()

    override fun onCreatePresenterView(): MultipleMainContract.View = this

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.multiple_main_fragment, container, false)
            init(this.container)
        }
        return this.container
    }

    private var currentSubIndex = 2
    private fun init(view: View?) {
        view ?: return

        view.findViewById<View>(R.id.multiple_fragment_top_call_overlay)?.setOnProtectClickListener {
            addChildFragment(
                R.id.multiple_fragment_overlay_container,
                SubOverlayFragment.newInstance("1"),
                FragmentAddingOption(isReplace = false, isAddToBackStack = true)
            ) { requestKey, bundle ->
                val isOk = BundleUtil.isNavigationResultOk(bundle)
                Log.d("Herry", "requestKey= $requestKey, isOk = $isOk")
            }
        }

        view.findViewById<View>(R.id.multiple_fragment_call_sub)?.setOnProtectClickListener {
            addChildFragment(
                R.id.multiple_fragment_sub1_container,
                SubOverlayFragment.newInstance((currentSubIndex++).toString()),
                FragmentAddingOption(isReplace = false, isAddToBackStack = true)
            ) { requestKey, bundle ->
                val isOk = BundleUtil.isNavigationResultOk(bundle)
                Log.d("Herry", "requestKey= $requestKey, isOk = $isOk")
            }
        }

        view.findViewById<View>(R.id.multiple_fragment_bottom_second)?.setOnProtectClickListener {
            this@MultipleMainFragment.view?.rootView?.parent
            addFragment(
                MultipleSecondFragment.newInstance()
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
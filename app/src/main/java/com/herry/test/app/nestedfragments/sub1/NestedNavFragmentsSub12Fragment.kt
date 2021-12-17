package com.herry.test.app.nestedfragments.sub1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.herry.libs.app.nav.NavBundleUtil
import com.herry.libs.helper.ToastHelper
import com.herry.libs.widget.extension.getNavCurrentDestinationID
import com.herry.libs.widget.extension.navigateTo
import com.herry.libs.widget.extension.notifyToNavHost
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavFragment
import com.herry.test.databinding.NestedNavFragmentsSub12FragmentBinding

class NestedNavFragmentsSub12Fragment : BaseNavFragment() {

    private var _binding: NestedNavFragmentsSub12FragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragmentsSub12FragmentBinding.inflate(inflater, container, false)

            binding.nestedNavFragmentsSub2FragmentGoSub3.setOnClickListener { view ->
                navigateTo(R.id.nested_nav_fragments_sub13_fragment)
//                { bundle ->
//                    val result = NavBundleUtil.isNavigationResultOk(bundle)
//                    val fromId = NavBundleUtil.fromNavigationId(bundle)
//
//                    ToastHelper.showToast(requireActivity(), "from R.id.nested_nav_fragments_sub3_fragment = ${R.id.nested_nav_fragments_sub13_fragment == fromId}, result = $result")
//                }
            }

            binding.nestedNavFragmentsSub2FragmentShowOverlay.setOnClickListener {
                notifyToNavHost(
                    bundleOf(
                        "from" to getNavCurrentDestinationID(),
                        "value" to ""
                    )
                )
            }
        }

        return binding.root
    }

    override fun getNavigateUpResult(): Bundle {
        return NavBundleUtil.createNavigationBundle(true)
    }
}

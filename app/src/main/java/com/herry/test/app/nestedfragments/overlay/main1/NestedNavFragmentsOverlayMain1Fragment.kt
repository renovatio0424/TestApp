package com.herry.test.app.nestedfragments.overlay.main1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.libs.app.nav.NavBundleUtil
import com.herry.libs.helper.ToastHelper
import com.herry.libs.widget.extension.navigateTo
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavFragment
import com.herry.test.databinding.NestedNavFragmentsOverlayMain1FragmentBinding

class NestedNavFragmentsOverlayMain1Fragment : BaseNavFragment() {
    private var _binding: NestedNavFragmentsOverlayMain1FragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragmentsOverlayMain1FragmentBinding.inflate(inflater, container, false)

            binding.nestedNavFragmentsOverlayMain1FragmentGoSub.setOnClickListener { view ->
                navigateTo(R.id.nested_nav_fragments_overlay_main1_sub1_fragment)
//                { bundle ->
//                    val result = NavBundleUtil.isNavigationResultOk(bundle)
//                    val fromId = NavBundleUtil.fromNavigationId(bundle)
//
//                    ToastHelper.showToast(requireActivity(), "from R.id.nested_nav_fragments_overlay_main1_sub1_fragment = ${R.id.nested_nav_fragments_overlay_main1_sub1_fragment == fromId}, result = $result")
//                }
            }
        }

        return binding.root
    }
}
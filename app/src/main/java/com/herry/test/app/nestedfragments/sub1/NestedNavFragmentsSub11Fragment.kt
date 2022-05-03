package com.herry.test.app.nestedfragments.sub1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.libs.app.nav.NavBundleUtil
import com.herry.libs.helper.ToastHelper
import com.herry.libs.widget.extension.navigateTo
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavFragment
import com.herry.test.databinding.NestedNavFragmentsSub11FragmentBinding

class NestedNavFragmentsSub11Fragment : BaseNavFragment() {

    private var _binding: NestedNavFragmentsSub11FragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragmentsSub11FragmentBinding.inflate(inflater, container, false)

            binding.nestedNavFragmentsSub1FragmentGoSub2.setOnClickListener { view ->
                navigateTo(destinationId = R.id.nested_nav_fragments_sub12_fragment)
//                { bundle ->
//                    val result = NavBundleUtil.isNavigationResultOk(bundle)
//                    val fromId = NavBundleUtil.fromNavigationId(bundle)
//
//                    ToastHelper.showToast(requireActivity(), "from R.id.nested_nav_fragments_sub2_fragment = ${R.id.nested_nav_fragments_sub12_fragment == fromId}, result = $result")
//                }
            }
        }

        return binding.root
    }
}

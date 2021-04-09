package com.herry.test.app.nestedfragments.nav.overlay.main1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.herry.libs.helper.ToastHelper
import com.herry.libs.util.BundleUtil
import com.herry.libs.widget.extension.navigate
import com.herry.test.R
import com.herry.test.app.base.nestednav.BaseNestedNavFragment
import com.herry.test.databinding.NestedNavFragmentsOverlayMain1FragmentBinding

class NestedNavFragmentsOverlayMain1Fragment : BaseNestedNavFragment() {
    private var _binding: NestedNavFragmentsOverlayMain1FragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: NestedNavFragmentsOverlayMain1ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NestedNavFragmentsOverlayMain1ViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragmentsOverlayMain1FragmentBinding.inflate(inflater, container, false)

            binding.nestedNavFragmentsOverlayMain1FragmentGoSub.setOnClickListener { view ->
                navigate(R.id.nested_nav_fragments_overlay_main1_sub1_fragment) { bundle ->
                    val result = BundleUtil.isNavigationResultOk(bundle)
                    val fromId = BundleUtil.fromNavigationId(bundle)

                    ToastHelper.showToast(requireActivity(), "from R.id.nested_nav_fragments_overlay_main1_sub1_fragment = ${R.id.nested_nav_fragments_overlay_main1_sub1_fragment == fromId}, result = $result")
                }
            }
        }

        return binding.root
    }
}
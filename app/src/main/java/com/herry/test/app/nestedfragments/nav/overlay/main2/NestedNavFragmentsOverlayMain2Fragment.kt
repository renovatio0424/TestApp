package com.herry.test.app.nestedfragments.nav.overlay.main2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.herry.libs.util.AppUtil
import com.herry.libs.util.BundleUtil
import com.herry.libs.widget.extension.popToNavHost
import com.herry.test.app.base.nestednav.BaseNestedNavFragment
import com.herry.test.databinding.NestedNavFragmentsOverlayMain2FragmentBinding

class NestedNavFragmentsOverlayMain2Fragment : BaseNestedNavFragment() {
    private var _binding: NestedNavFragmentsOverlayMain2FragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: NestedNavFragmentsOverlayMain2ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NestedNavFragmentsOverlayMain2ViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragmentsOverlayMain2FragmentBinding.inflate(inflater, container, false)

            binding.nestedNavFragmentsOverlayMain2FragmentClose.setOnClickListener { view ->
                popToNavHost(BundleUtil.createNavigationBundle(false))
                AppUtil.pressBackKey(requireActivity())
            }
        }

        return binding.root
    }
}
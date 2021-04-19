package com.herry.test.app.nestedfragments.nav.overlay.main1.sub1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.libs.app.nav.NavBundleUtil
import com.herry.libs.util.BundleUtil
import com.herry.libs.widget.extension.popToNavHost
import com.herry.test.app.base.nav.BaseNavFragment
import com.herry.test.app.base.nestednav.BaseNestedNavFragment
import com.herry.test.databinding.NestedNavFragmentsOverlayMain1Sub1FragmentBinding

class NestedNavFragmentsOverlayMain1Sub1Fragment : BaseNavFragment() {
    private var _binding: NestedNavFragmentsOverlayMain1Sub1FragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragmentsOverlayMain1Sub1FragmentBinding.inflate(inflater, container, false)

            binding.nestedNavFragmentsOverlayMain1Sub1FragmentClose.setOnClickListener { view ->
                popToNavHost(NavBundleUtil.createNavigationBundle(false))
            }
        }

        return binding.root
    }
}
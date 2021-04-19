package com.herry.test.app.nestedfragments.nav.second

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.libs.app.nav.NavBundleUtil
import com.herry.libs.util.BundleUtil
import com.herry.test.app.base.nav.BaseNavFragment
import com.herry.test.app.base.nestednav.BaseNestedNavFragment
import com.herry.test.databinding.NestedNavFragmentsSecondFragmentBinding

class NestedNavFragmentsSecondFragment : BaseNavFragment() {

    private var _binding: NestedNavFragmentsSecondFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragmentsSecondFragmentBinding.inflate(inflater, container, false)
        }

        return binding.root
    }

    override fun onNavigateUpResult(): Bundle {
        return NavBundleUtil.createNavigationBundle(true)
    }
}

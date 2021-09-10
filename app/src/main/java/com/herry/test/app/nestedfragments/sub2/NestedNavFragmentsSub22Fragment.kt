package com.herry.test.app.nestedfragments.sub2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.libs.app.nav.NavBundleUtil
import com.herry.test.app.base.nav.BaseNavFragment
import com.herry.test.databinding.NestedNavFragmentsSub22FragmentBinding

class NestedNavFragmentsSub22Fragment : BaseNavFragment() {

    private var _binding: NestedNavFragmentsSub22FragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragmentsSub22FragmentBinding.inflate(inflater, container, false)
        }

        return binding.root
    }
}

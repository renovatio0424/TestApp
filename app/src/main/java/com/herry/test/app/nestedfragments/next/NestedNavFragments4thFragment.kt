package com.herry.test.app.nestedfragments.next

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.libs.app.nav.NavBundleUtil
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavFragment
import com.herry.test.databinding.NestedNavFragments4thFragmentBinding

class NestedNavFragments4thFragment : BaseNavFragment() {

    private var _binding: NestedNavFragments4thFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragments4thFragmentBinding.inflate(inflater, container, false)

            binding.nestedNavFragments4thFragmentGoToFirst.setOnClickListener {
                navigateUp(NavBundleUtil.addNavigationUpDestinationId(desId = R.id.nested_nav_fragments_main_fragment).apply {
                    putInt("aa", 111)
                })
            }
        }

        return binding.root
    }
}

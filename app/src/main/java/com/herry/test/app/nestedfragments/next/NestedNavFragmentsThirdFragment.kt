package com.herry.test.app.nestedfragments.next

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.libs.widget.extension.navigateTo
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavFragment
import com.herry.test.databinding.NestedNavFragmentsThirdFragmentBinding

class NestedNavFragmentsThirdFragment : BaseNavFragment() {

    private var _binding: NestedNavFragmentsThirdFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragmentsThirdFragmentBinding.inflate(inflater, container, false)

            binding.nestedNavFragmentsThirdFragmentGoToNext.setOnClickListener {
                navigateTo(R.id.nested_nav_fragments_4th_fragment)
            }
        }

        return binding.root
    }
}

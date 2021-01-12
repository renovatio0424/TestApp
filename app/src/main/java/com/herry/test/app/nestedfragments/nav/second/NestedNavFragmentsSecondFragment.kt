package com.herry.test.app.nestedfragments.nav.second

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.herry.libs.util.BundleUtil
import com.herry.test.app.base.nestednav.BaseNestedNavFragment
import com.herry.test.databinding.NestedNavFragmentsSecondFragmentBinding

class NestedNavFragmentsSecondFragment : BaseNestedNavFragment() {

    private var _binding: NestedNavFragmentsSecondFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: NestedNavFragmentsSecondViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NestedNavFragmentsSecondViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragmentsSecondFragmentBinding.inflate(inflater, container, false)
        }

        return binding.root
    }

    override fun onNavigateUpResult(): Bundle? {
        return BundleUtil.createNavigationBundle(true)
    }
}

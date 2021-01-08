package com.herry.test.app.nestedfragments.nav.sub.sub1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.herry.test.R
import com.herry.test.app.nestedfragments.nav.sub.sub2.NestedNavFragmentsSub2ViewModel
import com.herry.test.databinding.NestedNavFragmentsSub1FragmentBinding

class NestedNavFragmentsSub1Fragment : Fragment() {

    private var _binding: NestedNavFragmentsSub1FragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: NestedNavFragmentsSub2ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NestedNavFragmentsSub2ViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragmentsSub1FragmentBinding.inflate(inflater, container, false)

            binding.nestedNavFragmentsSub1FragmentGoSub2.setOnClickListener { view ->
                view.findNavController().navigate(R.id.nested_nav_fragments_sub2_fragment)
            }
        }

        return binding.root
    }
}

package com.herry.test.app.nestedfragments.nav.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.herry.libs.widget.extension.findNestedNavHostFragment
import com.herry.libs.widget.extension.navigate
import com.herry.libs.widget.extension.setNestedNavHostFragmentResultListener
import com.herry.test.R
import com.herry.test.app.base.nestednav.BaseNestedNavFragment
import com.herry.test.app.nestedfragments.nav.second.NestedNavFragmentsSecondViewModel
import com.herry.test.databinding.NestedNavFragmentsMainFragmentBinding

class NestedNavFragmentsMainFragment : BaseNestedNavFragment() {

    private var _binding: NestedNavFragmentsMainFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: NestedNavFragmentsSecondViewModel

    private var subNavHostFragment: NavHostFragment? = null

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
            _binding = NestedNavFragmentsMainFragmentBinding.inflate(inflater, container, false)

            val subNavHost = findNestedNavHostFragment(binding.nestedNavFragmentsMainFragmentSubContainer.id)
            if (subNavHost != null) {
                subNavHostFragment = subNavHost
                addSubNavHostFragment(subNavHostFragment)

                setNestedNavHostFragmentResultListener(subNavHost) { _, bundle ->
                    onSubScreenResults(bundle)
                }
            }

            binding.nestedNavFragmentsMainFragmentBottomShowSub3.setOnClickListener {
                subNavHostFragment?.navigate(R.id.nested_nav_fragments_sub3_fragment)
            }

            binding.nestedNavFragmentsMainFragmentBottomShowSecond.setOnClickListener {
                navigate(R.id.action_nested_nav_fragments_main_to_second) { _ ->
                    Toast.makeText(requireContext(), "from second screen by action id", Toast.LENGTH_SHORT).show()
                }
            }

            binding.nestedNavFragmentsMainFragmentBottomShowSecondWithId.setOnClickListener {
                navigate(R.id.nested_nav_fragments_second_fragment) { _ ->
                    Toast.makeText(requireContext(), "from second screen by destination id", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    private fun onSubScreenResults(bundle: Bundle) {
        when (bundle.getInt("from")) {
            R.id.nested_nav_fragments_sub1_fragment -> {
                Toast.makeText(requireContext(), "from sub 1", Toast.LENGTH_SHORT).show()
            }
            R.id.nested_nav_fragments_sub2_fragment -> {
                Toast.makeText(requireContext(), "from sub 2", Toast.LENGTH_SHORT).show()
            }
            R.id.nested_nav_fragments_sub3_fragment -> {
                Toast.makeText(requireContext(), "from sub 3", Toast.LENGTH_SHORT).show()
                navigate(NestedNavFragmentsMainFragmentDirections.actionNestedNavFragmentsMainToSecond()) { _ ->

                }
            }
        }
    }

}

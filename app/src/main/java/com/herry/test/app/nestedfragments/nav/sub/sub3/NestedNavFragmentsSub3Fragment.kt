package com.herry.test.app.nestedfragments.nav.sub.sub3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.herry.libs.widget.extension.getNavCurrentDestinationID
import com.herry.libs.widget.extension.setNestedNavFragmentResult
import com.herry.test.databinding.NestedNavFragmentsSub3FragmentBinding

class NestedNavFragmentsSub3Fragment : Fragment() {

    private var _binding: NestedNavFragmentsSub3FragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: NestedNavFragmentsSub3ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NestedNavFragmentsSub3ViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragmentsSub3FragmentBinding.inflate(inflater, container, false)

            binding.nestedNavFragmentsSub3FragmentShowSecondScreen.setOnClickListener { view ->
                setNestedNavFragmentResult(
                    bundleOf(
                        "from" to getNavCurrentDestinationID(),
                        "value" to ""
                    )
                )
            }

            binding.nestedNavFragmentsSub3FragmentPopupSubs.setOnClickListener {
            }
        }

        return binding.root
    }
}

package com.herry.test.app.nestedfragments.nav.sub.sub2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.herry.libs.helper.ToastHelper
import com.herry.libs.util.BundleUtil
import com.herry.libs.widget.extension.getNavCurrentDestinationID
import com.herry.libs.widget.extension.navigate
import com.herry.libs.widget.extension.notifyToNavHost
import com.herry.test.R
import com.herry.test.app.base.nestednav.BaseNestedNavFragment
import com.herry.test.databinding.NestedNavFragmentsSub2FragmentBinding

class NestedNavFragmentsSub2Fragment : BaseNestedNavFragment() {

    private var _binding: NestedNavFragmentsSub2FragmentBinding? = null
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
            _binding = NestedNavFragmentsSub2FragmentBinding.inflate(inflater, container, false)

            binding.nestedNavFragmentsSub2FragmentGoSub3.setOnClickListener { view ->
                navigate(R.id.nested_nav_fragments_sub3_fragment) { bundle ->
                    val result = BundleUtil.isNavigationResultOk(bundle)
                    val fromId = BundleUtil.fromNavigationId(bundle)

                    ToastHelper.showToast(requireActivity(), "from R.id.nested_nav_fragments_sub3_fragment = ${R.id.nested_nav_fragments_sub3_fragment == fromId}, result = $result")
                }
            }

            binding.nestedNavFragmentsSub2FragmentShowOverlay.setOnClickListener {
                notifyToNavHost(
                    bundleOf(
                        "from" to getNavCurrentDestinationID(),
                        "value" to ""
                    )
                )
            }
        }

        return binding.root
    }

    override fun onNavigateUpResult(): Bundle {
        return BundleUtil.createNavigationBundle(true)
    }
}

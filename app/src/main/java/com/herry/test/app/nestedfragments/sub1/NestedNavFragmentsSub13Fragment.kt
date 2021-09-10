package com.herry.test.app.nestedfragments.sub1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.herry.libs.widget.extension.getNavCurrentDestinationID
import com.herry.libs.widget.extension.notifyToNavHost
import com.herry.libs.widget.extension.popToNavHost
import com.herry.test.app.base.nav.BaseNavFragment
import com.herry.test.databinding.NestedNavFragmentsSub13FragmentBinding

class NestedNavFragmentsSub13Fragment : BaseNavFragment() {

    private var _binding: NestedNavFragmentsSub13FragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragmentsSub13FragmentBinding.inflate(inflater, container, false)

            binding.nestedNavFragmentsSub3FragmentShowSecondScreen.setOnClickListener { view ->
                notifyToNavHost(
                    bundleOf(
                        "from" to getNavCurrentDestinationID(),
                        "value" to ""
                    )
                )
            }

            binding.nestedNavFragmentsSub3FragmentPopupSubs.setOnClickListener {
                popToNavHost()
            }
        }

        return binding.root
    }
}

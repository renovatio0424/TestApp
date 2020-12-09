package com.herry.test.app.multiple_fragments.overlay

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.test.R
import com.herry.test.app.base.nested.NestedView

class SubOverlayFragment: NestedView<SubOverlayContract.View, SubOverlayContract.Presenter>(), SubOverlayContract.View {

    companion object {
        const val ARG_NAME = "ARG_NAME"

        fun newInstance(name: String = ""): SubOverlayFragment = SubOverlayFragment().apply {
            this.arguments = createArguments().apply {
                putString(ARG_NAME, name)
            }
        }
    }

    private var container: View? = null
    private var name: TextView? = null

    override fun onCreatePresenter(): SubOverlayContract.Presenter {
        return SubOverlayPresenter(name = this.arguments?.getString(ARG_NAME))
    }

    override fun onCreatePresenterView(): SubOverlayContract.View = this

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.sub_overlay_fragment, container, false)

            init(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return

        name = view.findViewById(R.id.sub_overlay_fragment_name)

        view.findViewById<View>(R.id.sub_overlay_fragment_close)?.setOnProtectClickListener {
            finishAndResults(true)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onLaunched(name: String) {
        this.name?.text = "Sub Overlay: $name"
    }
}
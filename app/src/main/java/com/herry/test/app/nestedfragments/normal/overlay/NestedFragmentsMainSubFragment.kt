package com.herry.test.app.nestedfragments.normal.overlay

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.test.R
import com.herry.test.app.base.nested.BaseNestedView

class NestedFragmentsMainSubFragment: BaseNestedView<NestedFragmentsMainSubContract.View, NestedFragmentsMainSubContract.Presenter>(), NestedFragmentsMainSubContract.View {

    companion object {
        const val ARG_NAME = "ARG_NAME"

        fun newInstance(name: String = ""): NestedFragmentsMainSubFragment = NestedFragmentsMainSubFragment().apply {
            this.arguments = createArguments().apply {
                putString(ARG_NAME, name)
            }
        }
    }

    private var container: View? = null
    private var name: TextView? = null

    override fun onCreatePresenter(): NestedFragmentsMainSubContract.Presenter {
        return NestedFragmentsMainSubPresenter(name = this.arguments?.getString(ARG_NAME))
    }

    override fun onCreatePresenterView(): NestedFragmentsMainSubContract.View = this

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.nested_fragment_main_sub_fragment, container, false)

            init(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return

        name = view.findViewById(R.id.nested_fragment_main_sub_fragment_name)

        view.findViewById<View>(R.id.nested_fragment_main_sub_fragment_close)?.setOnProtectClickListener {
            finishAndResults(true)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onLaunched(name: String) {
        this.name?.text = "Sub Overlay: $name"
    }
}
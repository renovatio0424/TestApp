package com.herry.test.app.nestedfragments.normal.second

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.test.R
import com.herry.test.app.base.nested.BaseNestedView
import com.herry.test.widget.TitleBarForm

class NestedFragmentsSecondFragment: BaseNestedView<NestedFragmentsSecondContract.View, NestedFragmentsSecondContract.Presenter>(), NestedFragmentsSecondContract.View {

    private var container: View? = null

    companion object {
        fun newInstance(): NestedFragmentsSecondFragment = NestedFragmentsSecondFragment().apply {
            this.arguments = createArguments()
        }
    }
    override fun onCreatePresenter(): NestedFragmentsSecondContract.Presenter = NestedFragmentsSecondPresenter()

    override fun onCreatePresenterView(): NestedFragmentsSecondContract.View = this

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == this.container) {
            this.container = inflater.inflate(R.layout.nested_fragment_second_fragment, container, false)
            init(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return

        TitleBarForm(
            activity = requireActivity(),
            onClickBack = {
                setResult(true, null)
            }
        ).apply {
            bindFormHolder(view.context, view.findViewById(R.id.nested_fragment_second_fragment_title))
            bindFormModel(view.context, TitleBarForm.Model(title = "Multiple Second Screen"))
        }
    }
}
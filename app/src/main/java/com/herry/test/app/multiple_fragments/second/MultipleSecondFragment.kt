package com.herry.test.app.multiple_fragments.second

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.test.R
import com.herry.test.app.base.nested.NestedView
import com.herry.test.widget.TitleBarForm

class MultipleSecondFragment: NestedView<MultipleSecondContract.View, MultipleSecondContract.Presenter>(), MultipleSecondContract.View {

    private var container: View? = null

    companion object {
        fun newInstance(): MultipleSecondFragment = MultipleSecondFragment().apply {
            this.arguments = createArguments()
        }
    }
    override fun onCreatePresenter(): MultipleSecondContract.Presenter = MultipleSecondPresenter()

    override fun onCreatePresenterView(): MultipleSecondContract.View = this

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == this.container) {
            this.container = inflater.inflate(R.layout.multiple_second_fragment, container, false)
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
            bindFormHolder(view.context, view.findViewById(R.id.multiple_second_fragment_title))
            bindFormModel(view.context, TitleBarForm.Model(title = "Multiple Second Screen"))
        }
    }
}
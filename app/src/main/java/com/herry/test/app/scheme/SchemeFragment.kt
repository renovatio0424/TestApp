package com.herry.test.app.scheme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.model.NodeRoot
import com.herry.libs.nodeview.recycler.NodeRecyclerAdapter
import com.herry.libs.nodeview.recycler.NodeRecyclerForm
import com.herry.libs.util.AppUtil
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.test.R
import com.herry.test.app.base.BaseView
import com.herry.test.widget.TitleBarForm
import kotlinx.android.synthetic.main.main_test_item.view.*
import kotlinx.android.synthetic.main.scheme_fragment.view.*

/**
 * Created by herry.park on 2020/06/11.
 **/
class SchemeFragment : BaseView<SchemeContract.View, SchemeContract.Presenter>(), SchemeContract.View {

    override fun onCreatePresenter(): SchemeContract.Presenter? = SchemePresenter()

    override fun onCreatePresenterView(): SchemeContract.View = this

    override val root: NodeRoot
        get() = adapter.root

    private val adapter: Adapter = Adapter()

    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == this.container) {
            this.container = inflater.inflate(R.layout.scheme_fragment, container, false)
            init(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return

        TitleBarForm(
            activity = requireActivity(),
            onClickBack = { AppUtil.pressBackKey(requireActivity(), view) }
        ).apply {
            bindFormHolder(view.context, view.scheme_fragment_title)
            bindFormModel(view.context, TitleBarForm.Model(title = "Scheme Intent", backEnable = true))
        }

        view.scheme_fragment_list.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            if (itemAnimator is SimpleItemAnimator) {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            adapter = this@SchemeFragment.adapter
        }
    }

    override fun onGotoScheme(scheme: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(scheme)))
    }

    inner class Adapter: NodeRecyclerAdapter(::requireContext) {
        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
            list.add(SchemeItemForm())
        }
    }

    private inner class SchemeItemForm : NodeForm<SchemeItemForm.Holder, SchemeContract.SchemeItemType>(Holder::class, SchemeContract.SchemeItemType::class) {
        inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
            init {
                view.setOnProtectClickListener {
                    NodeRecyclerForm.getBindModel(this@SchemeItemForm, this@Holder)?.let {
                        presenter?.gotoScheme(it)
                    }
                }
            }
        }

        override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

        override fun onLayout(): Int = R.layout.main_test_item

        override fun onBindModel(context: Context, holder: Holder, model: SchemeContract.SchemeItemType) {
            holder.view.main_test_item_title.text = when (model) {
                SchemeContract.SchemeItemType.EFFECT -> "Effect"
                SchemeContract.SchemeItemType.OVERLAY_STICKER -> "Overlay(Sticker)"
                SchemeContract.SchemeItemType.OVERLAY_TEXT -> "Overlay(Text)"
                SchemeContract.SchemeItemType.TEXT_ARABIC -> "Text(Arabic)"
            }
        }
    }
}
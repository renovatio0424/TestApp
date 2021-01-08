package com.herry.test.app.intent.scheme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.herry.test.app.base.nav.BaseNavView
import com.herry.test.widget.TitleBarForm

/**
 * Created by herry.park on 2020/06/11.
 **/
class SchemeFragment : BaseNavView<SchemeContract.View, SchemeContract.Presenter>(), SchemeContract.View {

    override fun onCreatePresenter(): SchemeContract.Presenter = SchemePresenter()

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
            bindFormHolder(view.context, view.findViewById(R.id.scheme_fragment_title))
            bindFormModel(view.context, TitleBarForm.Model(title = "Scheme Intent", backEnable = true))
        }

        view.findViewById<RecyclerView>(R.id.scheme_fragment_list)?.apply {
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
            val title: TextView? = view.findViewById(R.id.main_test_item_title)
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
            holder.title?.text = when (model) {
                SchemeContract.SchemeItemType.EFFECT -> "Effect"
                SchemeContract.SchemeItemType.OVERLAY_STICKER -> "Overlay(Sticker)"
                SchemeContract.SchemeItemType.OVERLAY_TEXT -> "Overlay(Text)"
                SchemeContract.SchemeItemType.TEXT_ARABIC -> "Text(Arabic)"
                SchemeContract.SchemeItemType.ASSET_DYNAMIC_LINK -> "Asset dynamic link"
                SchemeContract.SchemeItemType.KINEMASTER_DEEP_LINK -> "KineMaster (Deep Link)"
                SchemeContract.SchemeItemType.KINEMASTER_DINAMIC_LINK -> "KineMaster (Dynamic Link)"
                SchemeContract.SchemeItemType.PROJECT_FEED_HOME -> "Project Feed Home (Deep Link)"
                SchemeContract.SchemeItemType.PROJECT_FEED_HOME_DYNAMIC_LINK -> "Project Feed Home (Dynamic Link)"
                SchemeContract.SchemeItemType.PROJECT_FEED_CATEGORY -> "Project Feed Category (Deep Link)"
                SchemeContract.SchemeItemType.PROJECT_FEED_CATEGORY_DYNAMIC_LINK -> "Project Feed Category (Dynamic Link)"
                SchemeContract.SchemeItemType.PROJECT_FEED_SEARCH -> "Project Feed Search (Deep Link)"
                SchemeContract.SchemeItemType.PROJECT_FEED_SEARCH_DYNAMIC_LINK -> "Project Feed Search (Dynamic Link)"
                SchemeContract.SchemeItemType.PROJECT_FEED_DETAIL -> "Project Feed Detail (Deep Link)"
                SchemeContract.SchemeItemType.PROJECT_FEED_DETAIL_DYNAMIC_LINK -> "Project Feed Detail (Dynamic Link)"
            }
        }
    }
}
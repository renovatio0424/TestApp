package com.herry.test.app.intent.list

import android.content.Context
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
import com.herry.test.R
import com.herry.test.app.base.BaseView
import com.herry.libs.app.activity_caller.module.ACNavigation
import com.herry.test.app.base.ac.AppACNavigation
import com.herry.test.app.intent.scheme.SchemeFragment
import com.herry.test.app.intent.share.ShareMediaListFragment
import com.herry.test.widget.TitleBarForm
import kotlinx.android.synthetic.main.intent_list_fragment.view.*
import kotlinx.android.synthetic.main.main_test_item.view.*

/**
 * Created by herry.park on 2020/06/11.
 **/
class IntentListFragment : BaseView<IntentListContract.View, IntentListContract.Presenter>(), IntentListContract.View {

    override fun onCreatePresenter(): IntentListContract.Presenter? = IntentListPresenter()

    override fun onCreatePresenterView(): IntentListContract.View = this

    override val root: NodeRoot
        get() = adapter.root

    private val adapter: Adapter = Adapter()

    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == this.container) {
            this.container = inflater.inflate(R.layout.intent_list_fragment, container, false)
            init(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return

        TitleBarForm(
            activity = requireActivity()
        ).apply {
            bindFormHolder(view.context, view.intent_list_fragment_title)
            bindFormModel(view.context, TitleBarForm.Model(title = "Test List"))
        }

        view.intent_list_fragment_list.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            if (itemAnimator is SimpleItemAnimator) {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            adapter = this@IntentListFragment.adapter
        }
    }

    inner class Adapter: NodeRecyclerAdapter(::requireContext) {
        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
            list.add(TestItemForm())
        }
    }

    override fun onScreen(type: IntentListContract.TestItemType) {
        when (type) {
            IntentListContract.TestItemType.SCHEME_TEST -> aC?.call(AppACNavigation.SingleCaller(SchemeFragment::class))
            IntentListContract.TestItemType.MEDIA_SHARE_TEST -> aC?.call(AppACNavigation.SingleCaller(ShareMediaListFragment::class))
        }
    }

    private inner class TestItemForm : NodeForm<TestItemForm.Holder, IntentListContract.TestItemType>(Holder::class, IntentListContract.TestItemType::class) {
        inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
            init {
                view.setOnClickListener {
                    NodeRecyclerForm.getBindModel(this@TestItemForm, this@Holder)?.let {
                        presenter?.moveToScreen(it)
                    }
                }
            }
        }

        override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

        override fun onLayout(): Int = R.layout.main_test_item

        override fun onBindModel(context: Context, holder: TestItemForm.Holder, model: IntentListContract.TestItemType) {
            holder.view.main_test_item_title.text = when (model) {
                IntentListContract.TestItemType.SCHEME_TEST -> "Scheme Intent"
                IntentListContract.TestItemType.MEDIA_SHARE_TEST -> "Media Share Intent"
            }
        }
    }
}
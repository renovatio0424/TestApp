package com.herry.test.app.nestedfragments.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.herry.libs.app.activity_caller.module.ACNavigation
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.model.NodeRoot
import com.herry.libs.nodeview.recycler.NodeRecyclerAdapter
import com.herry.libs.nodeview.recycler.NodeRecyclerForm
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavView
import com.herry.test.app.nestedfragments.nav.NestedNavFragmentsActivity
import com.herry.test.app.nestedfragments.normal.NestedFragmentsActivity
import com.herry.test.widget.TitleBarForm

/**
 * Created by herry.park on 2020/06/11.
 **/
class NestedFragmentsListFragment : BaseNavView<NestedFragmentsListContract.View, NestedFragmentsListContract.Presenter>(), NestedFragmentsListContract.View {

    override fun onCreatePresenter(): NestedFragmentsListContract.Presenter = NestedFragmentsListPresenter()

    override fun onCreatePresenterView(): NestedFragmentsListContract.View = this

    override val root: NodeRoot
        get() = adapter.root

    private val adapter: Adapter = Adapter()

    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == this.container) {
            this.container = inflater.inflate(R.layout.nested_fragments_list_fragment, container, false)
            init(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return

        TitleBarForm(
            activity = requireActivity()
        ).apply {
            bindFormHolder(view.context, view.findViewById(R.id.nested_fragments_list_fragment_title))
            bindFormModel(view.context, TitleBarForm.Model(title = "Nested Fragments List"))
        }

        view.findViewById<RecyclerView>(R.id.nested_fragments_list_fragment_list)?.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            if (itemAnimator is SimpleItemAnimator) {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            adapter = this@NestedFragmentsListFragment.adapter
        }
    }

    inner class Adapter: NodeRecyclerAdapter(::requireContext) {
        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
            list.add(TestItemForm())
        }
    }

    override fun onScreen(type: NestedFragmentsListContract.TestItemType) {
        when (type) {
            NestedFragmentsListContract.TestItemType.NORMAL -> {
                activityCaller?.call(
                    ACNavigation.IntentCaller(
                    Intent(requireActivity(), NestedFragmentsActivity::class.java), result = { resultCode, _, _ ->
                        if (resultCode == Activity.RESULT_OK) {
                            Log.d("Herry", "result = OK")
                        }
                    }
                ))
            }
            NestedFragmentsListContract.TestItemType.NAVIGATION -> {
//                navController()?.navigate(R.id.nested_nav_fragments_navigation)
                activityCaller?.call(
                    ACNavigation.IntentCaller(
                        Intent(requireActivity(), NestedNavFragmentsActivity::class.java), result = { resultCode, _, _ ->
                            if (resultCode == Activity.RESULT_OK) {
                                Log.d("Herry", "result = OK")
                            }
                        }
                    ))
            }
        }
    }

    private inner class TestItemForm : NodeForm<TestItemForm.Holder, NestedFragmentsListContract.TestItemType>(Holder::class, NestedFragmentsListContract.TestItemType::class) {
        inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
            val title: TextView? = view.findViewById(R.id.main_test_item_title)
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

        override fun onBindModel(context: Context, holder: TestItemForm.Holder, model: NestedFragmentsListContract.TestItemType) {
            holder.title?.text = when (model) {
                NestedFragmentsListContract.TestItemType.NORMAL -> "Normal"
                NestedFragmentsListContract.TestItemType.NAVIGATION -> "Navigation"
            }
        }
    }
}
package com.herry.test.app.main

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.herry.test.app.base.activity_caller.module.ACNavigation
import com.herry.test.app.base.activity_caller.module.ACPermission
import com.herry.test.app.checker.list.CheckerListFragment
import com.herry.test.app.gif.list.GifListFragment
import com.herry.test.app.intent.list.IntentListFragment
import com.herry.test.widget.TitleBarForm
import kotlinx.android.synthetic.main.main_fragment.view.*
import kotlinx.android.synthetic.main.main_test_item.view.*

/**
 * Created by herry.park on 2020/06/11.
 **/
class MainFragment : BaseView<MainContract.View, MainContract.Presenter>(), MainContract.View {

    override fun onCreatePresenter(): MainContract.Presenter? = MainPresenter()

    override fun onCreatePresenterView(): MainContract.View = this

    override val root: NodeRoot
        get() = adapter.root

    private val adapter: Adapter = Adapter()

    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == this.container) {
            this.container = inflater.inflate(R.layout.main_fragment, container, false)
            init(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return

        TitleBarForm(
            activity = requireActivity()
        ).apply {
            bindFormHolder(view.context, view.main_fragment_title)
            bindFormModel(view.context, TitleBarForm.Model(title = "Test List"))
        }

        view.main_fragment_list.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            if (itemAnimator is SimpleItemAnimator) {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            adapter = this@MainFragment.adapter
        }
    }

    inner class Adapter: NodeRecyclerAdapter(::requireContext) {
        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
            list.add(TestItemForm())
        }
    }

    override fun onScreen(type: MainContract.TestItemType) {
        when (type) {
            MainContract.TestItemType.SCHEME_TEST -> aC?.call(ACNavigation.SingleCaller(IntentListFragment::class))
            MainContract.TestItemType.GIF_DECODER -> {
                aC?.call(ACPermission.Caller(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    onGranted = {
                        Handler(Looper.getMainLooper()).post {
                            aC?.call(ACNavigation.SingleCaller(
                                GifListFragment::class
                            ))
                        }
                    }
                ))
            }
            MainContract.TestItemType.CHECKER_LIST -> aC?.call(ACNavigation.SingleCaller(CheckerListFragment::class))
        }
    }

    private inner class TestItemForm : NodeForm<TestItemForm.Holder, MainContract.TestItemType>(Holder::class, MainContract.TestItemType::class) {
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

        override fun onBindModel(context: Context, holder: TestItemForm.Holder, model: MainContract.TestItemType) {
            holder.view.main_test_item_title.text = when (model) {
                MainContract.TestItemType.SCHEME_TEST -> "Intent"
                MainContract.TestItemType.GIF_DECODER -> "GIF Decoder"
                MainContract.TestItemType.CHECKER_LIST -> "Checker List"
            }
        }
    }
}
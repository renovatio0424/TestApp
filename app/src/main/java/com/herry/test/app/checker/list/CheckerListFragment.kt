package com.herry.test.app.checker.list

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
import com.herry.libs.util.AppUtil
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.test.R
import com.herry.test.app.base.BaseView
import com.herry.test.widget.TitleBarForm
import kotlinx.android.synthetic.main.checker_list_fragment.view.*
import kotlinx.android.synthetic.main.main_test_item.view.*

/**
 * Created by herry.park on 2020/7/7
 **/
class CheckerListFragment : BaseView<CheckerListContract.View, CheckerListContract.Presenter>(), CheckerListContract.View {

    override fun onCreatePresenter(): CheckerListContract.Presenter? = CheckerListPresenter()

    override fun onCreatePresenterView(): CheckerListContract.View = this

    override val root: NodeRoot
        get() = adapter.root

    private val adapter: Adapter = Adapter()

    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == this.container) {
            this.container = inflater.inflate(R.layout.checker_list_fragment, container, false)
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
            bindFormHolder(view.context, view.checker_list_fragment_title)
            bindFormModel(view.context, TitleBarForm.Model(title = "Checker List", backEnable = true))
        }

        view.checker_list_fragment_list.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            if (itemAnimator is SimpleItemAnimator) {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            adapter = this@CheckerListFragment.adapter
        }
    }

    override fun onShow(item: CheckerListContract.ItemType) {
//        when(item) {
//            CheckerListContract.ItemType.CHANGE -> TODO()
//            CheckerListContract.ItemType.MANDATORY -> TODO()
//            CheckerListContract.ItemType.COMBINATION -> TODO()
//        }
    }

    inner class Adapter: NodeRecyclerAdapter(::requireContext) {
        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
            list.add(CheckerListItemForm())
        }
    }

    private inner class CheckerListItemForm : NodeForm<CheckerListItemForm.Holder, CheckerListContract.ItemType>(
        Holder::class, CheckerListContract.ItemType::class) {
        inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
            init {
                view.setOnProtectClickListener {
                    NodeRecyclerForm.getBindModel(this@CheckerListItemForm, this@Holder)?.let {
                        presenter?.show(it)
                    }
                }
            }
        }

        override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

        override fun onLayout(): Int = R.layout.main_test_item

        override fun onBindModel(context: Context, holder: Holder, model: CheckerListContract.ItemType) {
            holder.view.main_test_item_title.text = when (model) {
                CheckerListContract.ItemType.CHANGE -> "Checks Changed Data"
                CheckerListContract.ItemType.MANDATORY -> "Checks Mandatory Data"
                CheckerListContract.ItemType.COMBINATION -> "Checks Combination Data"
            }
        }
    }
}
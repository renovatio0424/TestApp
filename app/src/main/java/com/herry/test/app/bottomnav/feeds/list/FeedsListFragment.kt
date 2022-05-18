package com.herry.test.app.bottomnav.feeds.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.recycler.NodeRecyclerAdapter
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.extension.navigateTo
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.libs.widget.extension.setViewMargin
import com.herry.libs.widget.recyclerview.form.recycler.RecyclerForm
import com.herry.libs.widget.recyclerview.snap.PagerSnapExHelper
import com.herry.libs.widget.recyclerview.snap.PagerSnapWithTabLayoutHelper
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavView
import com.herry.test.widget.SingleLineChipsForm
import com.herry.test.widget.TabLayoutScrollForm


class FeedsListFragment: BaseNavView<FeedsListContract.View, FeedsListContract.Presenter>(), FeedsListContract.View {

    override fun onCreatePresenter(): FeedsListContract.Presenter = FeedsListPresenter()

    override fun onCreatePresenterView(): FeedsListContract.View = this

    private var container: View? = null

    private var recyclerForm: RecyclerForm? = null

    private val snapHelper = PagerSnapExHelper()
    private val adapter: Adapter = Adapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.feed_list_fragment, container, false)
            init(this.container)
        } else {
            // fixed: "java.lang.IllegalStateException: The specified child already has a parent.
            // You must call removeView() on the child's parent first."
            ViewUtil.removeViewFormParent(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        val context = view?.context ?: return

        view.findViewById<View>(R.id.feed_list_fragment_result)?.let { resultView ->
            resultView.setViewMargin(0, ViewUtil.getStatusBarHeight(context), 0, 0)
            resultView.setOnProtectClickListener {
                navigateTo(destinationId = R.id.feed_search_fragment)
            }
        }

        val tabLayoutForm = TabLayoutScrollForm()
        view.findViewById<View>(R.id.feed_list_fragment_feeds)?.let { tabLayoutFormView ->
            tabLayoutForm.bindHolder(context, tabLayoutFormView)
        }

        recyclerForm = object: RecyclerForm() {
            override fun onBindRecyclerView(context: Context, recyclerView: RecyclerView) {
                val tabLayout = tabLayoutForm.getTabLayout()
                if (tabLayout != null) {
                    recyclerView.setHasFixedSize(true)
                    recyclerView.layoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
                    recyclerView.itemAnimator = null
                    recyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING)

                    snapHelper.attachToRecyclerView(recyclerView)
                    recyclerView.adapter = this@FeedsListFragment.adapter

                    PagerSnapWithTabLayoutHelper(tabLayout, snapHelper, object : PagerSnapWithTabLayoutHelper.OnListener {
                        override fun onSnapped(position: Int) {
                        }

                        override fun onUnsnapped(position: Int) {
                        }
                    })
                }
            }
        }
        view.findViewById<View>(R.id.feed_list_fragment_feeds)?.let { recyclerFormView ->
            recyclerForm?.bindHolder(context, recyclerFormView)
        }
    }

    override fun onUpdateCategories(categories: SingleLineChipsForm.Chips, current: Int) {
        val context = this.context ?: return
    }

    inner class Adapter: NodeRecyclerAdapter(::requireContext), PagerSnapWithTabLayoutHelper.PagerSnapWithTabLayoutHelperPageTitle {
        private val categories: MutableList<FeedsListContract.Categories> = mutableListOf<FeedsListContract.Categories>().apply {
            addAll(FeedsListContract.Categories.values())
        }

        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
//            list.add(ListItemForm { model ->
//                ToastHelper.run { showToast(requireActivity(), model.name) }
//            })
        }

        override fun getPageTitle(position: Int): String? {
            return if (position >= 0 && position < categories.size) {
                categories[position].title
            } else {
                null
            }
        }

    }
}
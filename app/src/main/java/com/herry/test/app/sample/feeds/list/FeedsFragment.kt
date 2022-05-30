package com.herry.test.app.sample.feeds.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.model.NodeRoot
import com.herry.libs.nodeview.recycler.NodeRecyclerAdapter
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.extension.navigateTo
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.libs.widget.extension.setViewPaddingTop
import com.herry.libs.widget.view.recyclerview.snap.PagerSnapExHelper
import com.herry.libs.widget.view.recyclerview.snap.PagerSnapWithTabLayoutHelper
import com.herry.test.R
import com.herry.test.app.base.ScreenWindowStyle
import com.herry.test.app.base.StatusBarStyle
import com.herry.test.app.base.nav.BaseNavView
import com.herry.test.app.sample.feeds.detail.FeedDetailFragment
import com.herry.test.widget.TabLayoutForm


class FeedsFragment: BaseNavView<FeedsContract.View, FeedsContract.Presenter>(), FeedsContract.View {

    override fun onScreenWindowStyle(): ScreenWindowStyle = ScreenWindowStyle(true, StatusBarStyle.LIGHT)

    override fun onCreatePresenter(): FeedsContract.Presenter = FeedsPresenter()

    override fun onCreatePresenterView(): FeedsContract.View = this

    override val categoryFeedsRoot: NodeRoot
        get() = categoryFeedsAdapter.root

    private val categoryFeedsAdapter: CategoryFeedsAdapter = CategoryFeedsAdapter()

    private var pagerSnapWithTabLayoutHelper: PagerSnapWithTabLayoutHelper? = null

    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.feeds_fragment, container, false)
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

        view.findViewById<View>(R.id.feeds_fragment_search_container)?.setViewPaddingTop(ViewUtil.getStatusBarHeight(context))
        view.findViewById<View>(R.id.feeds_fragment_search)?.let { resultView ->
            resultView.setOnProtectClickListener {
                navigateTo(destinationId = R.id.feed_search_fragment)
            }
        }

        val categoriesContainer = view.findViewById<View>(R.id.feeds_fragment_categories_form_container)
        view.findViewById<AppBarLayout>(R.id.feeds_fragment_app_bar_layout)?.let {
            it.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val scrollRange = appBarLayout.totalScrollRange
                if (scrollRange > 0) {
                    val topPadding = (kotlin.math.abs(verticalOffset / scrollRange.toFloat()) * ViewUtil.getStatusBarHeight(context)).toInt()
                    categoriesContainer?.setPadding(0, topPadding, 0, 0)
                }
            })
        }

        val categoriesForm = TabLayoutForm(
            tabMode = TabLayoutForm.TabMode.AUTO,
            gapWidth = ViewUtil.getDimensionPixelSize(context, R.dimen.size10)
        ).also {
            it.bindHolder(context, view, R.id.feeds_fragment_categories_form)
        }

        val tabLayout = categoriesForm.getTabLayout()
        val recyclerView: RecyclerView? = view.findViewById(R.id.feeds_fragment_category_feeds)
        if (tabLayout != null && recyclerView != null) {
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            recyclerView.itemAnimator = null
            recyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING)

            val pagerSnapHelper = PagerSnapExHelper().also { pagerSnapHelper ->
                pagerSnapHelper.attachToRecyclerView(recyclerView)
            }
            recyclerView.adapter = this@FeedsFragment.categoryFeedsAdapter

            pagerSnapWithTabLayoutHelper = PagerSnapWithTabLayoutHelper(tabLayout, pagerSnapHelper, object : PagerSnapWithTabLayoutHelper.OnListener {
                override fun onSnapped(position: Int) {
                    presenter?.setCurrentCategory(position)
                }

                override fun onUnsnapped(position: Int) {}
            })
        }
    }

    override fun onScrollToCategory(position: Int) {
        pagerSnapWithTabLayoutHelper?.updateTabs(position)
    }

    inner class CategoryFeedsAdapter : NodeRecyclerAdapter(::requireContext), PagerSnapWithTabLayoutHelper.PagerSnapWithTabLayoutHelperPageTitle {
        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
            list.add(FeedCategoryFeedsForm(
                onErrorCB = {
                },
                onRefresh = {
                },
                onClickFeed = { coverView, feed ->
                    presenter?.getFeedDetailCallData(feed)?.let { callData ->
                        navigateTo(destinationId = R.id.feed_detail_fragment, args = FeedDetailFragment.createArguments(callData), navigatorExtras = FeedDetailFragment.createNavigatorExtra(coverView, feed))
                    }
                }
            ))
        }

        override fun getPageTitle(position: Int): String? {
            return presenter?.getCategoryName(position)
        }
    }
}
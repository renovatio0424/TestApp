package com.herry.test.app.bottomnav.feeds.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.model.NodeRoot
import com.herry.libs.nodeview.recycler.NodeRecyclerAdapter
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.extension.navigateTo
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.libs.widget.extension.setViewMargin
import com.herry.libs.widget.extension.setViewPadding
import com.herry.libs.widget.recyclerview.endless.EndlessRecyclerViewScrollListener
import com.herry.libs.widget.recyclerview.form.recycler.RecyclerEmptyTextForm
import com.herry.libs.widget.recyclerview.form.recycler.RecyclerForm
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavView
import com.herry.test.repository.feed.db.Feed
import com.herry.test.widget.SingleLineChipsForm
import java.util.*


class FeedsListFragment: BaseNavView<FeedsListContract.View, FeedsListContract.Presenter>(), FeedsListContract.View {

    override fun onCreatePresenter(): FeedsListContract.Presenter = FeedsListPresenter(FeedsListContract.Categories.ALL)

    override fun onCreatePresenterView(): FeedsListContract.View = this

    override val root: NodeRoot
        get() = adapter.root

    private val adapter: Adapter = Adapter()

    private var container: View? = null

    private var recyclerForm = object: RecyclerForm() {
        private fun calculateSpanCounts(context: Context): Int {
            val defaultMargin = 8 // 8dp
            val defaultSpanCounts = 2
            val screenSize = ViewUtil.getScreenSize(context)
            val spanCounts = if (screenSize.width <= 0) {
                defaultSpanCounts
            } else {
                ((defaultSpanCounts * ViewUtil.convertPixelsToDp(screenSize.width.toFloat()).toInt()) / (360 - (defaultSpanCounts + 1) * defaultMargin))
            }

            return spanCounts
        }

        override fun onBindRecyclerView(context: Context, recyclerView: RecyclerView) {
            recyclerView.setViewPadding(ViewUtil.getDimensionPixelSize(context, R.dimen.size08))
            recyclerView.setHasFixedSize(true)
            val layoutManager = StaggeredGridLayoutManager(calculateSpanCounts(context), StaggeredGridLayoutManager.VERTICAL)
            recyclerView.layoutManager = layoutManager
            recyclerView.itemAnimator = null

            recyclerView.adapter = this@FeedsListFragment.adapter

            recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val spanCount = (recyclerView.layoutManager as? StaggeredGridLayoutManager)?.spanCount ?: return
                    val positionArray = IntArray(spanCount)
                    val firstVisiblePosition =(recyclerView.layoutManager as? StaggeredGridLayoutManager)?.findFirstCompletelyVisibleItemPositions(positionArray) ?: positionArray

                    if (firstVisiblePosition.isNotEmpty()) {
                        val firstItemPosition = positionArray[0]
                        if (firstItemPosition >= 0) {
                            presenter?.setCurrentPosition(firstItemPosition)
                        }
                    }
                }
            })
            endlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    presenter?.loadMore()
                }
            }.also { listener ->
                recyclerView.addOnScrollListener(listener)
            }
        }
    }

    private var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null

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

        view.findViewById<View>(R.id.feed_list_fragment_feeds)?.let { recyclerFormView ->
            recyclerForm.bindHolder(context, recyclerFormView)
        }
    }

    override fun onUpdateCategories(categories: SingleLineChipsForm.Chips, current: Int) {
//        val context = this.context ?: return
    }

    override fun onLaunched(counts: Int) {
        if (0 < counts) {
            recyclerForm.setEmptyView(null)
            recyclerForm.scrollToPosition(0)
            endlessRecyclerViewScrollListener?.resetState()
        } else {
            val emptyTextForm = RecyclerEmptyTextForm()
            emptyTextForm.createFormHolder(requireContext(), recyclerForm.getEmptyParentView())
            emptyTextForm.bindFormModel(requireContext(), "Empty feeds")
            recyclerForm.setEmptyView(emptyTextForm.getView())
        }
    }

    override fun onScrollToPosition(position: Int) {
        recyclerForm.scrollToPosition(position, null)
    }

    inner class Adapter: NodeRecyclerAdapter(::requireContext) {
        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
            list.add(FeedListItemForm())
        }
    }

    private inner class FeedListItemForm: NodeForm<FeedListItemForm.Holder, Feed>(Holder::class, Feed::class) {
        inner class Holder(context: Context, view: View): NodeHolder(context, view) {
            val container: View? = view.findViewById(R.id.feed_list_item_form_container)
            val cover: ImageView? = view.findViewById(R.id.feed_list_item_form_cover)
        }

        override fun onLayout(): Int = R.layout.feed_list_item_form

        override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

        override fun onBindModel(context: Context, holder: Holder, model: Feed) {
            val constraintLayout = holder.view as? ConstraintLayout
            if (constraintLayout != null) {
                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)
                holder.container?.let { container ->
                    val width = model.width
                    val height = model.height
                    val dimensionRatio = String.format(Locale.ENGLISH, "H,%d:%d", width, height)
                    constraintSet.setDimensionRatio(container.id, dimensionRatio)
                    constraintSet.applyTo(constraintLayout)
                }
            }

            holder.cover?.let { cover ->
                Glide.with(context).load(model.imagePath).into(cover)
            }
        }
    }
}
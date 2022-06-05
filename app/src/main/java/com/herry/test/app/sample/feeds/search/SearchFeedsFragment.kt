package com.herry.test.app.sample.feeds.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
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
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.libs.widget.view.recyclerview.form.recycler.RecyclerForm
import com.herry.test.R
import com.herry.test.app.base.ScreenWindowStyle
import com.herry.test.app.base.StatusBarStyle
import com.herry.test.app.base.nav.BaseNavView
import com.herry.test.app.sample.repository.database.searchlog.RecentlySearchKeyword

class SearchFeedsFragment: BaseNavView<SearchFeedsContract.View, SearchFeedsContract.Presenter>(), SearchFeedsContract.View {

    override fun onScreenWindowStyle(): ScreenWindowStyle = ScreenWindowStyle(false, StatusBarStyle.LIGHT)

    override fun onCreatePresenter(): SearchFeedsContract.Presenter = SearchFeedsPresenter()

    override fun onCreatePresenterView(): SearchFeedsContract.View = this

    private var container: View? = null

    private val keywordForm = SearchInputEditForm(
        onTextChanged = { text ->
            presenter?.searchFeed(text)
        },
        onFocusChange = { _, _ ->
            // updates search button
        },
        onEditorActionListener = { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                AppUtil.pressBackKey(activity, container?.rootView)
                true
            } else {
                false
            }
        })

    override val recentlyRoot: NodeRoot
        get() = recentlyAdapter.root

    private val recentlyAdapter: RecentlyAdapter = RecentlyAdapter()

    private val recentlyRecyclerForm = object : RecyclerForm() {
        override fun onBindRecyclerView(context: Context, recyclerView: RecyclerView) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                setHasFixedSize(true)
                if (itemAnimator is SimpleItemAnimator) {
                    (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                }
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        when(newState) {
                            RecyclerView.SCROLL_STATE_DRAGGING -> {
                                // hide softkeyboard and remove focus
                                ViewUtil.hideSoftKeyboard(requireContext(), container?.rootView)
                                keywordForm.requestFocus(false)
                            }
                        }
                    }
                })
            }
            recyclerView.adapter = recentlyAdapter
        }
    }

    override val searchRoot: NodeRoot
        get() = searchAdapter.root

    override fun onChangedViewMode(mode: SearchFeedsContract.ViewMode) {
        when (mode) {
            SearchFeedsContract.ViewMode.RECENTLY -> {
                recentlyRecyclerForm.setVisibility(View.VISIBLE)
                searchRecyclerForm.setVisibility(View.INVISIBLE)
            }
            SearchFeedsContract.ViewMode.SEARCH -> {
                searchRecyclerForm.setVisibility(View.VISIBLE)
                recentlyRecyclerForm.setVisibility(View.INVISIBLE)
            }
        }
    }

    private val searchAdapter: SearchAdapter = SearchAdapter()

    private val searchRecyclerForm = object : RecyclerForm() {
        override fun onBindRecyclerView(context: Context, recyclerView: RecyclerView) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                setHasFixedSize(true)
                if (itemAnimator is SimpleItemAnimator) {
                    (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                }
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        when(newState) {
                            RecyclerView.SCROLL_STATE_DRAGGING -> {
                                // hide softkeyboard and remove focus
                                ViewUtil.hideSoftKeyboard(requireContext(), container?.rootView)
                                keywordForm.requestFocus(false)
                            }
                        }
                    }
                })
            }
            recyclerView.adapter = searchAdapter
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.search_feeds_fragment, container, false)
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

        view.findViewById<View>(R.id.search_feeds_fragment_search_keyword_form)?.run {
            keywordForm.bindHolder(context, this)
        }

        view.findViewById<View>(R.id.search_feeds_fragment_recently)?.run {
            recentlyRecyclerForm.bindHolder(context, this)
        }

        view.findViewById<View>(R.id.search_feeds_fragment_searched_feeds)?.run {
            searchRecyclerForm.bindHolder(context, this)
        }
    }

    inner class RecentlyAdapter: NodeRecyclerAdapter(::requireContext) {
        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
//            list.add(RecentHeadForm())
            list.add(RecentForm(
                onClickKeyword = { model ->

                },
                onClickDelete = { model ->

                }
            ))
        }
    }

    inner class SearchAdapter: NodeRecyclerAdapter(::requireContext) {
        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
//            list.add(EmptyForm())
//            list.add(AutoCompleteHotelForm(
//                onVolleyMultiPostImageLoader = ::volleyMultiPostImageLoader,
//                onClick = {
//                    presenter?.showHotelDetail(it)
//                }
//            ))
        }
    }

    private inner class RecentForm(
        val onClickKeyword: (keyword: RecentlySearchKeyword) -> Unit,
        val onClickDelete: (keyword: RecentlySearchKeyword) -> Unit,
    ) : NodeForm<RecentForm.Holder, RecentlySearchKeyword>(Holder::class, RecentlySearchKeyword::class) {

        inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
            val icon: ImageView? = view.findViewById(R.id.recent_keyword_list_item_icon)
            val keyword: TextView? = view.findViewById(R.id.recent_keyword_list_item_keyword)
            val delete: View? = view.findViewById(R.id.recent_keyword_list_item_delete)

            init {
                view.setOnProtectClickListener {
                    NodeRecyclerForm.getBindModel(this@RecentForm, this@Holder)?.let { model ->
                        onClickKeyword(model)
                    }
                }
                delete?.setOnProtectClickListener {
                    NodeRecyclerForm.getBindModel(this@RecentForm, this@Holder)?.let { model ->
                        onClickDelete(model)
                    }
                }
            }
        }

        override fun onLayout(): Int = R.layout.recent_keyword_list_item

        override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

        override fun onBindModel(context: Context, holder: Holder, model: RecentlySearchKeyword) {
            holder.keyword?.text = model.keyword
        }
    }
}
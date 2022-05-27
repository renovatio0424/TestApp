package com.herry.test.app.bottomnav.hots

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
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.view.recyclerview.endless.EndlessRecyclerViewScrollListener
import com.herry.libs.widget.view.recyclerview.snap.PagerSnapExHelper
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavView
import com.herry.test.app.bottomnav.hots.forms.FeedForm

class NewFragment: BaseNavView<NewContract.View, NewContract.Presenter>(), NewContract.View {

    override fun onCreatePresenter(): NewContract.Presenter = NewPresenter()

    override fun onCreatePresenterView(): NewContract.View = this

    override val root: NodeRoot
        get() = adapter.root

    private val adapter: Adapter = Adapter()

    private var container: View? = null

    private var snapHelper: PagerSnapExHelper? = null

    private var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.new_fragment, container, false)
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

        view.findViewById<RecyclerView>(R.id.new_fragment_list)?.let { recyclerView ->
            val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(true)
            if (recyclerView.itemAnimator is SimpleItemAnimator) {
                (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            recyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING)
            recyclerView.adapter = this@NewFragment.adapter

            snapHelper = PagerSnapExHelper().apply {
                setOnSnappedListener(object: PagerSnapExHelper.OnSnappedListener {
                    override fun onSnapped(position: Int, itemCount: Int) {
                        presenter?.setCurrentPosition(position)
                        presenter?.play(position)
                    }

                    override fun onUnsnapped(position: Int, itemCount: Int) {
//                        presenter?.stop(position)
                    }
                })
            }

            snapHelper?.attachToRecyclerView(recyclerView)

            endlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    presenter?.loadMore()
                }
            }.also { listener ->
                recyclerView.addOnScrollListener(listener)
            }
        }
    }

    override fun onLaunched(count: Int) {
        if (0 < count) {
            onScrollTo(0)
            endlessRecyclerViewScrollListener?.resetState()
        }
    }

    override fun onScrollTo(position: Int) {
        snapHelper?.scrollToSnapPosition(position, true)
    }

    inner class Adapter: NodeRecyclerAdapter(::requireContext) {
        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
            list.add(FeedForm(
                onAttachedVideoView = { model ->
                    presenter?.preparePlayer(model)
                },
                onDetachedVideoView = { model ->
                    presenter?.stop(model)
                },
                onTogglePlayer = { form, holder ->
                    presenter?.togglePlay(NodeRecyclerForm.getBindModel(form, holder))
                },
                onTogglePlayerVolume = { form, holder ->
                    presenter?.let { presenter ->
                        presenter.toggleVolume(NodeRecyclerForm.getBindModel(form, holder))
                        true
                    } ?: false
                }
            ))
        }
    }

}
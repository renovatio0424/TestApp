package com.herry.test.app.intent.share

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.herry.test.data.MediaFileInfoData
import com.herry.test.widget.TitleBarForm
import kotlinx.android.synthetic.main.file_list_item.view.*
import kotlinx.android.synthetic.main.share_media_list_fragment.view.*

/**
 * Created by herry.park on 2020/06/11.
 **/
class ShareMediaListFragment : BaseView<ShareMediaListContract.View, ShareMediaListContract.Presenter>(), ShareMediaListContract.View {

    override fun onCreatePresenter(): ShareMediaListContract.Presenter? = ShareMediaListPresenter()

    override fun onCreatePresenterView(): ShareMediaListContract.View = this

    override val root: NodeRoot
        get() = adapter.root

    private val adapter: Adapter = Adapter()

    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == this.container) {
            this.container = inflater.inflate(R.layout.share_media_list_fragment, container, false)
            init(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return

        TitleBarForm(
            activity = requireActivity()
        ).apply {
            bindFormHolder(view.context, view.share_media_list_fragment_title)
            bindFormModel(view.context, TitleBarForm.Model(title = "Share Media List"))
        }

        view.share_media_list_fragment_list.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            if (itemAnimator is SimpleItemAnimator) {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            adapter = this@ShareMediaListFragment.adapter
        }
    }

    inner class Adapter: NodeRecyclerAdapter(::requireContext) {
        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
            list.add(FileListItemForm())
        }
    }

    override fun onShare(content: MediaFileInfoData) {
        aC?.call(
            ACNavigation.IntentCaller(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, Uri.parse(content.path))
                type = content.mimeType
            }
        ))
    }

    private inner class FileListItemForm : NodeForm<FileListItemForm.Holder, MediaFileInfoData>(Holder::class, MediaFileInfoData::class) {
        inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
            init {
                view.setOnClickListener {
                    NodeRecyclerForm.getBindModel(this@FileListItemForm, this@Holder)?.let {
                        presenter?.share(it)
                    }
                }
            }
        }

        override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

        override fun onLayout(): Int = R.layout.file_list_item

        override fun onBindModel(context: Context, holder: Holder, model: MediaFileInfoData) {
            holder.view.file_list_item_name.text = model.name
        }
    }
}
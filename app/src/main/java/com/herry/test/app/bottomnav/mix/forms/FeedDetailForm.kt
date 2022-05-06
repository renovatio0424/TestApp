package com.herry.test.app.bottomnav.mix.forms

import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.recycler.NodeRecyclerForm
import com.herry.test.R

class FeedDetailForm(
    private val onAttachedVideoView: (form: FeedDetailForm, holder: FeedDetailForm.Holder) -> Unit,
    private val onDetachedVideoView: (form: FeedDetailForm, holder: FeedDetailForm.Holder) -> Unit
): NodeForm<FeedDetailForm.Holder, FeedDetailForm.Model> (Holder::class, Model::class), NodeRecyclerForm {
    data class Model(
        val id: String,
        val url: String
    )

    inner class Holder(context: Context, view: View): NodeHolder(context, view) {
        val videoView: StyledPlayerView = view.findViewById(R.id.feed_detail_form_video_view)
        val id: TextView = view.findViewById(R.id.feed_detail_form_id)

        init {
            videoView.useController = false
        }
    }

    override fun onLayout(): Int = R.layout.feed_detail_form

    override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

    override fun onBindModel(context: Context, holder: Holder, model: Model) {
        holder.id.text = model.id
    }

    override fun onViewRecycled(context: Context, holder: NodeHolder) {
    }

    override fun onViewAttachedToWindow(context: Context, holder: NodeHolder) {
        (holder as? Holder)?.let { formHolder ->
            onAttachedVideoView(this@FeedDetailForm, formHolder)
        }
    }

    override fun onViewDetachedFromWindow(context: Context, holder: NodeHolder) {
        (holder as? Holder)?.let { formHolder ->
            onDetachedVideoView(this@FeedDetailForm, formHolder)
        }
    }
}

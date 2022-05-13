package com.herry.test.app.bottomnav.hots.forms

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.recycler.NodeRecyclerForm
import com.herry.test.R

class FeedForm(
    private val onAttachedVideoView: (form: FeedForm, holder: FeedForm.Holder) -> Unit,
    private val onDetachedVideoView: (form: FeedForm, holder: FeedForm.Holder) -> Unit,
    private val onTogglePlayer: (form: FeedForm, holder: FeedForm.Holder) -> Unit
): NodeForm<FeedForm.Holder, FeedForm.Model> (Holder::class, Model::class), NodeRecyclerForm {
    data class Model(
        val id: String,
        val url: String
    )

    inner class Holder(context: Context, view: View): NodeHolder(context, view) {
        val videoView: StyledPlayerView = view.findViewById(R.id.feed_form_video_view)
        val id: TextView = view.findViewById(R.id.feed_form_id)
        val playStatus: View = view.findViewById(R.id.feed_form_play_status)

        val videoViewPlayerListener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                playStatus.isVisible = !isPlaying
            }
        }

        init {
            view.setOnClickListener {
                onTogglePlayer(this@FeedForm, this)
            }
            videoView.useController = false
        }
    }

    override fun onLayout(): Int = R.layout.feed_form

    override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

    override fun onBindModel(context: Context, holder: Holder, model: Model) {
        holder.id.text = model.id
    }

    override fun onViewRecycled(context: Context, holder: NodeHolder) {
    }

    override fun onViewAttachedToWindow(context: Context, holder: NodeHolder) {
        (holder as? Holder)?.let { formHolder ->
            onAttachedVideoView(this@FeedForm, formHolder)
            formHolder.videoView.player?.removeListener(formHolder.videoViewPlayerListener)
            formHolder.videoView.player?.addListener(formHolder.videoViewPlayerListener)
        }
    }

    override fun onViewDetachedFromWindow(context: Context, holder: NodeHolder) {
        (holder as? Holder)?.let { formHolder ->
            formHolder.videoView.player?.removeListener(formHolder.videoViewPlayerListener)
            onDetachedVideoView(this@FeedForm, formHolder)
        }
    }
}

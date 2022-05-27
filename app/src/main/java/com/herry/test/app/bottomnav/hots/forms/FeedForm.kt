package com.herry.test.app.bottomnav.hots.forms

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.recycler.NodeRecyclerForm
import com.herry.libs.nodeview.recycler.NodeRecyclerHolder
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.anim.ViewAnimCreator
import com.herry.libs.widget.anim.ViewAnimListener
import com.herry.libs.widget.anim.ViewAnimPlayer
import com.herry.test.R
import com.herry.test.repository.feed.db.Feed
import java.util.*

class FeedForm(
    private val onAttachedVideoView: (model: Model?) -> ExoPlayer?,
    private val onDetachedVideoView: (model: Model?) -> Unit,
    private val onTogglePlayer: (form: FeedForm, holder: FeedForm.Holder) -> Unit
): NodeForm<FeedForm.Holder, FeedForm.Model> (Holder::class, Model::class), NodeRecyclerForm {
    data class Model(
        val index: Int,
        val feed: Feed
    )

    inner class Holder(context: Context, view: View): NodeHolder(context, view) {
        val videoView: StyledPlayerView? = view.findViewById(R.id.feed_form_video_view)
        val id: TextView? = view.findViewById(R.id.feed_form_id)
        val playStatus: View? = view.findViewById(R.id.feed_form_play_status)
        val cover: ImageView? = view.findViewById(R.id.feed_form_cover)

        val videoViewPlayerListener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
//                val model = NodeRecyclerForm.getBindModel(this@FeedForm, this@Holder)
//                Trace.d("Herry", "feed ${model?.index} isPlaying changed = $isPlaying")
                super.onIsPlayingChanged(isPlaying)
                cover?.let { cover ->
                    if (cover.isVisible && cover.alpha == 1f) {
                        ViewAnimPlayer().apply {
                            add(ViewAnimCreator(cover)
                                    .alpha(1f, 0f)
                                    .duration(1000L))
                            onStopListener = object : ViewAnimListener.OnStop {
                                override fun onStop() {
                                    cover.isVisible = false
                                }
                            }
                        }.also {
                            it.start()
                        }
                    } else {
                        cover.isVisible = false
                    }
                }
                playStatus?.isVisible = !isPlaying
            }
        }

        init {
            view.setOnClickListener {
                onTogglePlayer(this@FeedForm, this)
            }
            videoView?.useController = false
        }
    }

    override fun onLayout(): Int = R.layout.feed_form

    override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

    override fun onBindModel(context: Context, holder: Holder, model: Model) {
//        Trace.d("Herry", "onBindModel for ${model.index}")
        val constraintLayout = holder.view as? ConstraintLayout
        if (constraintLayout != null) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            holder.cover?.let { cover ->
                val width = model.feed.width
                val height = model.feed.height
                val dimensionRatio = String.format(Locale.ENGLISH, "${if (ViewUtil.isPortraitOrientation(context)) "H" else "W"},%d:%d", width, height)
                constraintSet.setDimensionRatio(cover.id, dimensionRatio)
                constraintSet.applyTo(constraintLayout)
            }
        }

        holder.id?.text = model.feed.projectId
        holder.cover?.let { cover ->
            cover.isVisible = !isAvailablePlaying(holder.videoView)
            cover.alpha = 1f
            Glide.with(context).load(model.feed.imagePath).into(cover)
        }
    }

    private fun isAvailablePlaying(videoView: StyledPlayerView?): Boolean {
        val exoPlayerPlaybackState = videoView?.player?.playbackState
        return (exoPlayerPlaybackState == ExoPlayer.STATE_READY && videoView.player?.isPlaying == true
                || exoPlayerPlaybackState == ExoPlayer.STATE_READY)
    }

    override fun onViewRecycled(context: Context, nodeRecyclerHolder: NodeRecyclerHolder) {
//        val model = NodeRecyclerForm.getBindModel(this@FeedForm, nodeRecyclerHolder)
//        Trace.d("Herry", "onViewRecycled for ${model?.index}")
    }

    override fun onViewAttachedToWindow(context: Context, nodeRecyclerHolder: NodeRecyclerHolder) {
        val holder = nodeRecyclerHolder.holder as? Holder ?: return
        val model = NodeRecyclerForm.getBindModel(this@FeedForm, nodeRecyclerHolder)
        val videoView = holder.videoView

//        Trace.d("Herry", "onViewAttachedToWindow for ${model?.index}")

        videoView?.player?.removeListener(holder.videoViewPlayerListener)
        videoView?.player = onAttachedVideoView(model)
        videoView?.player?.addListener(holder.videoViewPlayerListener)

        holder.cover?.isVisible = !isAvailablePlaying(videoView)
    }

    override fun onViewDetachedFromWindow(context: Context, nodeRecyclerHolder: NodeRecyclerHolder) {
        val holder = nodeRecyclerHolder.holder as? Holder ?: return
        val model = NodeRecyclerForm.getBindModel(this@FeedForm, nodeRecyclerHolder)
        val videoView = holder.videoView

//        Trace.d("Herry", "onViewDetachedFromWindow for ${model?.index}")
        videoView?.player?.removeListener(holder.videoViewPlayerListener)
        videoView?.player = null

        onDetachedVideoView(model)
    }
}

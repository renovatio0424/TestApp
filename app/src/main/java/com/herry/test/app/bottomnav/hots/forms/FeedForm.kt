package com.herry.test.app.bottomnav.hots.forms

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.recycler.NodeRecyclerForm
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.anim.ViewAnimCreator
import com.herry.libs.widget.anim.ViewAnimListener
import com.herry.libs.widget.anim.ViewAnimPlayer
import com.herry.test.R
import com.herry.test.repository.feed.db.Feed
import java.util.*

class FeedForm(
    private val onAttachedVideoView: (form: FeedForm, holder: FeedForm.Holder) -> Unit,
    private val onDetachedVideoView: (form: FeedForm, holder: FeedForm.Holder) -> Unit,
    private val onTogglePlayer: (form: FeedForm, holder: FeedForm.Holder) -> Unit
): NodeForm<FeedForm.Holder, FeedForm.Model> (Holder::class, Model::class), NodeRecyclerForm {
    data class Model(
        val feed: Feed
    )

    inner class Holder(context: Context, view: View): NodeHolder(context, view) {
        val videoView: StyledPlayerView? = view.findViewById(R.id.feed_form_video_view)
        val id: TextView? = view.findViewById(R.id.feed_form_id)
        val playStatus: View? = view.findViewById(R.id.feed_form_play_status)
        val cover: ImageView? = view.findViewById(R.id.feed_form_cover)

        val videoViewPlayerListener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
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
        val constraintLayout = holder.view as? ConstraintLayout
        if (constraintLayout != null) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            holder.cover?.let { preview ->
                val width = model.feed.width
                val height = model.feed.height
                val projectRatio = model.feed.ratio()
                val previewRatio = constraintLayout.width.toFloat() / constraintLayout.height.toFloat()
                val dimensionRatio = String.format(Locale.ENGLISH, "${if (projectRatio > previewRatio) "H" else "W"},%d:%d", width, height)
                constraintSet.setDimensionRatio(preview.id, dimensionRatio)
                constraintSet.applyTo(constraintLayout)
            }
        }

        holder.id?.text = model.feed.projectId
        val coverImage = holder.cover
        if (coverImage != null) {
            coverImage.isVisible = true
            coverImage.alpha = 1f
            Glide.with(context).load(model.feed.imagePath).into(coverImage)
        }
    }

    override fun onViewRecycled(context: Context, holder: NodeHolder) {
    }

    override fun onViewAttachedToWindow(context: Context, holder: NodeHolder) {
        (holder as? Holder)?.let { formHolder ->
            onAttachedVideoView(this@FeedForm, formHolder)
            formHolder.videoView?.player?.removeListener(formHolder.videoViewPlayerListener)
            formHolder.videoView?.player?.addListener(formHolder.videoViewPlayerListener)
        }
    }

    override fun onViewDetachedFromWindow(context: Context, holder: NodeHolder) {
        (holder as? Holder)?.let { formHolder ->
            formHolder.videoView?.player?.removeListener(formHolder.videoViewPlayerListener)
            onDetachedVideoView(this@FeedForm, formHolder)
        }
    }
}

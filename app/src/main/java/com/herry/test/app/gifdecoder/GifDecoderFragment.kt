package com.herry.test.app.gifdecoder

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.herry.libs.helper.ToastHelper
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.model.NodeHelper
import com.herry.libs.nodeview.recycler.NodeRecyclerAdapter
import com.herry.libs.nodeview.recycler.NodeRecyclerForm
import com.herry.libs.util.AppUtil
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.libs.widget.loopsnaprecyclerview.PagerSnapExHelper
import com.herry.test.R
import com.herry.test.app.base.BaseView
import com.herry.test.widget.TitleBarForm
import kotlinx.android.synthetic.main.gif_decoder_decoded_gif_frames.view.*
import kotlinx.android.synthetic.main.gif_decoder_fragment.view.*
import kotlinx.android.synthetic.main.gif_decoder_gif_frame.view.*
import kotlinx.android.synthetic.main.gif_decoder_gif_frames_indicator.view.*


/**
 * Created by herry.park on 2020/06/11.
 **/
class GifDecoderFragment : BaseView<GifDecoderContract.View, GifDecoderContract.Presenter>(), GifDecoderContract.View {

    override fun onCreatePresenter(): GifDecoderContract.Presenter? = GifDecoderPresenter()

    override fun onCreatePresenterView(): GifDecoderContract.View = this

    private var container: View? = null
    private var mediaInfoText: TextView? = null
    private var decodedFrames: GifFramesForm? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == this.container) {
            this.container = inflater.inflate(R.layout.gif_decoder_fragment, container, false)
            init(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return

        TitleBarForm(
            activity = requireActivity(),
            onClickBack = { AppUtil.pressBackKey(requireActivity(), view) }
        ).apply {
            bindFormHolder(view.context, view.gif_decoder_fragment_title)
            bindFormModel(view.context, TitleBarForm.Model(title = "Gif Decoder", backEnable = true))
        }

        mediaInfoText = view.gif_decoder_fragment_media_info
        view.gif_decoder_fragment_decode?.setOnClickListener {
            presenter?.decode("gif_netscape_iteration_0.gif")
        }

        decodedFrames = GifFramesForm(
            context = view.context,
            onClick = { position, model ->
                ToastHelper.showToast(activity, "$position frame (${model.delay})")
            }
        )
        decodedFrames?.bindFormHolder(view.context, view.gif_decoder_fragment_decoded_gif_frames)
    }

    override fun onResume() {
        super.onResume()

        // sets title
        activity?.actionBar?.apply {
            title = "Gif Decoder"
        }
    }

    override fun onLaunched() {
        mediaInfoText?.text = "gif_netscape_iteration_0.gif"
    }

    override fun onDecoded(mediaInfo: GifDecoderContract.GifMediaInfo) {
        mediaInfoText?.text = mediaInfo.toString()
    }

    override fun onDrawFrames(frames: GifDecoderContract.DecodedGifFrames) {
        decodedFrames?.bindFormModel(requireContext(), frames)
    }

    private class GifFramesForm(
        private val context: Context,
        private val onClick: ((position: Int, model: GifDecoderContract.DecodedGifFrame) -> Unit)?
    ) : NodeForm<GifFramesForm.Holder, GifDecoderContract.DecodedGifFrames>(Holder::class, GifDecoderContract.DecodedGifFrames::class) {
        inner class Adapter: NodeRecyclerAdapter(::context) {
            override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
                list.add(GifFrameForm(
                    onClick = { position, model ->
                        onClick?.let { it(position, model) }
                    }
                ))
            }
        }

        inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
            val snapHelper: PagerSnapExHelper = PagerSnapExHelper().apply {
                setOnSnappedListener(object: PagerSnapExHelper.OnSnappedListener {
                    override fun onSnapped(position: Int, itemCount: Int) {
                        setIndicator(this@Holder, position, itemCount)
                    }

                    override fun onUnsnapped(position: Int, itemCount: Int) {
                    }
                })
            }
            val adapter: Adapter = Adapter()

            val indicator: LinearLayout = view.gif_decoder_decoded_gif_frames_indicator

            init {
                val recyclerView = view.gif_decoder_decoded_gif_frames
                recyclerView?.let {
                    it.setHasFixedSize(true)
                    it.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                    it.itemAnimator = null
                    it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            recyclerView.parent.requestDisallowInterceptTouchEvent(true)
                        }
                    })

                    it.adapter = adapter
                    snapHelper.attachToRecyclerView(it)
                }
            }
        }

        override fun onLayout(): Int = R.layout.gif_decoder_decoded_gif_frames

        override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

        override fun onBindModel(context: Context, holder: Holder, model: GifDecoderContract.DecodedGifFrames) {
            holder.adapter.root.let { node ->
                node.beginTransition()
                NodeHelper.addModels(node, *model.frames.toTypedArray())
                node.endTransition()
            }

            holder.snapHelper.scrollToSnapPosition(0)
        }

        @SuppressLint("SetTextI18n")
        private fun setIndicator(holder: Holder, position: Int, count: Int) {
            ViewUtil.removeAllViews(holder.indicator)
            if (1 >= count) return

            for (index in 0 until count) {
                val view = ViewUtil.inflate(R.layout.gif_decoder_gif_frames_indicator, holder.indicator).apply {
                    this.gif_decoder_gif_frames_indicator_item_view.background = ViewUtil.getColorDrawable(this.context, if (index == position) R.color.tbc_10 else R.color.tbc_10_a40)
                }
                ViewUtil.addView(holder.indicator, view)
            }
        }
    }

    private class GifFrameForm(
        private val onClick: ((position: Int, model: GifDecoderContract.DecodedGifFrame) -> Unit)?
    ) : NodeForm<GifFrameForm.Holder, GifDecoderContract.DecodedGifFrame>(Holder::class, GifDecoderContract.DecodedGifFrame::class) {
        inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
            init {
                view.setOnProtectClickListener {
                    NodeRecyclerForm.getBindNode(this@GifFrameForm, this@Holder)?.let { node ->
                        onClick?.let { it(node.getViewPosition(), node.model) }
                    }
                }
            }
        }

        override fun onLayout(): Int = R.layout.gif_decoder_gif_frame

        override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

        @SuppressLint("SetTextI18n")
        override fun onBindModel(context: Context, holder: Holder, model: GifDecoderContract.DecodedGifFrame) {
            holder.view.gif_decoder_gif_frame_duration?.text = "${model.delay} ms"
            holder.view.gif_decoder_gif_frame_image?.setImageDrawable(BitmapDrawable(null, model.bitmap))
        }
    }
}
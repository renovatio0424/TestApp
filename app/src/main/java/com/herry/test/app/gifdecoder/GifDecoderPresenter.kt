package com.herry.test.app.gifdecoder

import android.util.Log
import com.herry.libs.media.gif.decoder.GifDecoder
import com.herry.libs.media.gif.decoder.GifHeader
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * Created by herry.park on 2020/06/11.
 **/
class GifDecoderPresenter : GifDecoderContract.Presenter() {

    private val decoder: GifDecoder = GifDecoder()

    override fun onLaunched(view: GifDecoderContract.View) {
        // sets list items
        view.onLaunched()
    }

    override fun decode(assets: String) {
        val data: ByteArray = getAssetByteArray(assets) ?: return

        decoder.setData(data)

        val header: GifHeader = decoder.header ?: return

        view?.onDecoded(
            GifDecoderContract.GifMediaInfo(
                name = assets,
                width = header.width,
                height = header.height,
                frameCounts = header.frameCount,
                totalDuration = decoder.getDuration()
            )
        )

        val frames = mutableListOf<GifDecoderContract.DecodedGifFrame>()
        do {
            val bitmap = decoder.getNextFrame()
            val currentFrameIndex = decoder.getCurrentFrameIndex()
            Log.d("Herry", "currentFrameIndex $currentFrameIndex")
            val delay = decoder.getDelay(currentFrameIndex)
            if (null != bitmap) {
                frames.add(GifDecoderContract.DecodedGifFrame(
                    bitmap = bitmap,
                    delay = delay
                ))
            }
            decoder.advance()
        } while (bitmap != null && frames.size < decoder.getFrameCount())

        view?.onDrawFrames(GifDecoderContract.DecodedGifFrames(frames))
        decoder.clear()
//        val bitmap = decoder.nextFrame
    }

    private fun getAssetByteArray(assets: String) : ByteArray? {
        val context = view?.getViewContext() ?: return null
        val inputStream: InputStream = context.assets.open(assets)

        val buffer = ByteArray(8192)
        var bytesRead: Int
        val output = ByteArrayOutputStream()
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
        return output.toByteArray()
    }
}
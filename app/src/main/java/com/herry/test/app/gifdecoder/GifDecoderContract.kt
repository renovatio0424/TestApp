package com.herry.test.app.gifdecoder

import android.graphics.Bitmap
import com.herry.libs.mvp.IMvpView
import com.herry.test.app.base.BasePresent
import java.io.Serializable

/**
 * Created by herry.park on 2020/06/11.
 **/
interface GifDecoderContract {

    interface View : IMvpView<Presenter> {
        fun onLaunched()
        fun onDecoded(mediaInfo: GifMediaInfo)
        fun onDrawFrames(frames: DecodedGifFrames)
    }

    abstract class Presenter : BasePresent<View>() {
        abstract fun decode(assets: String)
    }

    data class GifMediaInfo(
        val name: String = "",
        val width: Int = 0,
        val height: Int = 0,
        val frameCounts: Int = 0,
        val totalDuration: Long = 0L
    ) : Serializable

    data class DecodedGifFrame(
        val bitmap: Bitmap,
        val delay: Long
    )

    data class DecodedGifFrames(
        val frames: MutableList<DecodedGifFrame> = mutableListOf()
    )
}
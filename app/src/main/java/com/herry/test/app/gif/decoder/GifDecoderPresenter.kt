package com.herry.test.app.gif.decoder

import android.util.Log
import com.herry.libs.media.gif.decoder.GifDecoder
import com.herry.libs.media.gif.decoder.GifHeader
import com.herry.libs.util.FileUtil
import com.herry.test.data.GifMediaFileInfoData
import io.reactivex.Observable
import java.io.File


/**
 * Created by herry.park on 2020/06/11.
 **/
class GifDecoderPresenter(private val data: GifMediaFileInfoData) : GifDecoderContract.Presenter() {

    override fun onLaunched(view: GifDecoderContract.View) {
        Log.d("Herry", "filePath = ${data.path} ")
        decode()
    }

    private fun decode() {
        subscribeObservable(
            getDecodedGif(data)
            , {
                launched {
                    view?.onDecoded(it)
                }
            }
        )
    }

    private fun getDecodedGif(data: GifMediaFileInfoData) : Observable<GifDecoderContract.DecodedGifMediaInfo> {
        return Observable.create { emitter ->
            val rowData: ByteArray? = FileUtil.readFileToByteArray(File(data.path))
            rowData?.let { _rowData ->
                val decoder = GifDecoder()
                decoder.setData(_rowData)

                val header: GifHeader = decoder.header ?: run {
                    emitter.onError(Throwable())
                    return@create
                }

                val frames = mutableListOf<GifDecoderContract.DecodedGifFrame>()
                do {
                    val bitmap = decoder.getNextFrame()
                    val currentFrameIndex = decoder.getCurrentFrameIndex()
                    Log.d("Herry", "currentFrameIndex $currentFrameIndex")
                    val delay = decoder.getDelay(currentFrameIndex)
                    if (null != bitmap) {
                        frames.add(GifDecoderContract.DecodedGifFrame(
                            bitmap = bitmap,
                            delay = delay,
                            index = frames.size
                        ))
                    }
                    decoder.advance()
                } while (bitmap != null && frames.size < decoder.getFrameCount())

                val duration = decoder.getDuration()

                decoder.clear()

                emitter.onNext(
                    GifDecoderContract.DecodedGifMediaInfo(
                        data = data,
                        width = header.width,
                        height = header.height,
                        frameCounts = header.frameCount,
                        totalDuration = duration,
                        frames = GifDecoderContract.DecodedGifFrames(frames)
                ))

                emitter.onComplete()
                return@create
            } ?: emitter.onError(Throwable())
        }
    }
}
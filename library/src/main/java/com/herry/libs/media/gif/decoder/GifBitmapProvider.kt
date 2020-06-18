package com.herry.libs.media.gif.decoder

import android.graphics.Bitmap
import com.herry.libs.media.bitmap_recycler.BitmapPool
import com.herry.libs.media.bitmap_recycler.BitmapPoolAdapter

/**
 * Created by herry.park on 2020/06/17.
 **/
class GifBitmapProvider(
    private val bitmapPool: BitmapPool = BitmapPoolAdapter()
) : IGifBitmapProvider {

    override fun obtain(width: Int, height: Int, config: Bitmap.Config): Bitmap {
        return bitmapPool.getDirty(width, height, config)
    }

    override fun release(bitmap: Bitmap) {
        bitmapPool.put(bitmap)
    }

    override fun obtainByteArray(size: Int): ByteArray = ByteArray(size)

    override fun release(bytes: ByteArray) {
    }

    override fun obtainIntArray(size: Int): IntArray = IntArray(size)

    override fun release(array: IntArray) {
    }
}

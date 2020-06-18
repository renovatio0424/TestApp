package com.herry.libs.media.bitmap_recycler

import android.graphics.Bitmap

/**
 * Created by herry.park on 2020/06/17.
 **/
class BitmapPoolAdapter : BitmapPool {
    override val maxSize: Long = 0L

    override fun setSizeMultiplier(sizeMultiplier: Float) {
        // Do nothing.
    }

    override fun put(bitmap: Bitmap?) {
        bitmap?.recycle()
    }

    override fun get(width: Int, height: Int, config: Bitmap.Config?): Bitmap {
        return Bitmap.createBitmap(width, height, config!!)
    }

    override fun getDirty(width: Int, height: Int, config: Bitmap.Config?): Bitmap {
        return get(width, height, config)
    }

    override fun clearMemory() {
        // Do nothing.
    }

    override fun trimMemory(level: Int) {
        // Do nothing.
    }
}
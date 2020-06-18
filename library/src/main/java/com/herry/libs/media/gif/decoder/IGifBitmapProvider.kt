package com.herry.libs.media.gif.decoder

import android.graphics.Bitmap

/**
 * Created by herry.park on 2020/06/17.
 **/
/**
 * An interface that can be used to provide reused {@link Bitmap}s to avoid GCs
 * from constantly allocating {@link Bitmap}s for every frame.
 */
interface IGifBitmapProvider {
    /**
     * Returns an [Bitmap] with exactly the given dimensions and config.
     *
     * @param width  The width in pixels of the desired [Bitmap].
     * @param height The height in pixels of the desired [Bitmap].
     * @param config The [Bitmap.Config] of the desired [               ].
     */
    fun obtain(width: Int, height: Int, config: Bitmap.Config): Bitmap

    /**
     * Releases the given Bitmap back to the pool.
     */
    fun release(bitmap: Bitmap)

    /**
     * Returns a byte array used for decoding and generating the frame bitmap.
     *
     * @param size the size of the byte array to obtain
     */
    fun obtainByteArray(size: Int): ByteArray

    /**
     * Releases the given byte array back to the pool.
     */
    fun release(bytes: ByteArray)

    /**
     * Returns an int array used for decoding/generating the frame bitmaps.
     */
    fun obtainIntArray(size: Int): IntArray

    /**
     * Release the given array back to the pool.
     */
    fun release(array: IntArray)
}
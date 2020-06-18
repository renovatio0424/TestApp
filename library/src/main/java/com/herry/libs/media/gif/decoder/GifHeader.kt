package com.herry.libs.media.gif.decoder

import androidx.annotation.ColorInt

/**
 * Created by herry.park on 2020/06/17.
 **/
/**
 * A header object containing the number of frames in an animated GIF image as well as basic
 * metadata like width and height that can be used to decode each individual frame of the GIF. Can
 * be shared by one or more to play the same animated GIF in multiple views.
 *
 * @see <a href="https://www.w3.org/Graphics/GIF/spec-gif89a.txt">GIF 89a Specification</a>
 *          - Logical Screen Descriptor
 *          - Global Color Table.
 */
class GifHeader {
    @ColorInt
    var gct: IntArray? = null

    /**
     * Global status code of GIF data parsing.
     */
    var status = GifDecoder.GifDecodeStatus.OK

    /**
     * Total frame counts
     */
    var frameCount: Int = 0

    /**
     * Current frame
     */
    var currentFrame: GifFrame? = null

    /**
     * GIf Frames
     */
    val frames: MutableList<GifFrame> = mutableListOf()

    /**
     * Logical screen size: Full image width.
     */
    var width: Int = 0

    /**
     * Logical screen size: Full image height.
     */
    var height: Int= 0

    /**
     * Global color table flag
     */
    var globalColorTableFlag: Boolean = false

    /**
     * Size of Global Color Table.
     * The value is already computed to be a regular number, this field doesn't store the exponent.
     */
    var globalColorTableSize: Int = 0

    /**
     * Background color index into the Global/Local color table.
     */
    var backgroundColorIndex: Int = 0

    /**
     * Pixel aspect ratio.
     * Factor used to compute an approximation of the aspect ratio of the pixel in the original image.
     */
    var pixelAspectRatio = 0

    @ColorInt
    var backgroundColor: Int = 0
    var loopCount: Int = NETSCAPE_LOOP_COUNT_DOES_NOT_EXIST

    companion object {
        /**
         * The "Netscape" loop count which means loop forever.
         */
        const val NETSCAPE_LOOP_COUNT_FOREVER = 0

        /**
         * Indicates that this header has no "Netscape" loop count.
         */
        const val NETSCAPE_LOOP_COUNT_DOES_NOT_EXIST = -1
    }
}

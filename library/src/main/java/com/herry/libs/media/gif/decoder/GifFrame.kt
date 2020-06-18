package com.herry.libs.media.gif.decoder

import androidx.annotation.ColorInt

/**
 * Created by herry.park on 2020/06/17.
 **/

/**
 * Inner model class housing metadata for each frame.
 *
 * @see <a href="https://www.w3.org/Graphics/GIF/spec-gif89a.txt">GIF 89a Specification</a>
 *            - Image Descriptor
 *            - Local Color Table
 */
class GifFrame {

    /**
     * <p><b>GIF89a</b>:
     * <i>Indicates the way in which the graphic is to be treated after being displayed.</i></p>
     * Disposal methods 0-3 are defined, 4-7 are reserved for future use.
     */
    enum class Disposal(val value: Int) {
        /**
         * GIF Disposal Method meaning take no action.
         *
         * **GIF89a**: *No disposal specified.
         * The decoder is not required to take any action.*
         */
        UNSPECIFIED (0)

        /**
         * GIF Disposal Method meaning leave canvas from previous frame.
         *
         * **GIF89a**: *Do not dispose.
         * The graphic is to be left in place.*
         */
        , NONE (1)

        /**
         * GIF Disposal Method meaning clear canvas to background color.
         *
         * **GIF89a**: *Restore to background color.
         * The area used by the graphic must be restored to the background color.*
         */
        , BACKGROUND (2)

        /**
         * GIF Disposal Method meaning clear canvas to frame before last.
         *
         * **GIF89a**: *Restore to previous.
         * The decoder is required to restore the area overwritten by the graphic
         * with what was there prior to rendering the graphic.*
         */
        , PREVIOUS (3)
        ;

        companion object {
            @JvmStatic
            fun generate(value: Int) : Disposal = values().firstOrNull { it.value == value } ?: UNSPECIFIED
        }
    }

    /**
     * Image Left Position
     */
    var imageXPosition: Int = 0

    /**
     * Image Top Position
     */
    var imageYPosition: Int = 0

    /**
     * Image Width
     */
    var imageWidth: Int = 0

    /**
     * Image Height
     */
    var imageHeight: Int = 0

    /**
     * Control Flag.
     */
    var interlace: Boolean = false

    /**
     * Control Flag.
     */
    var transparency: Boolean = false

    /**
     * Disposal Method.
     */
    var dispose: Disposal = Disposal.UNSPECIFIED

    /**
     * Transparency Index.
     */
    var transparencyIndex: Int = 0

    /**
     * Delay, in milliseconds, to next frame.
     */
    var delay: Long = 0

    /**
     * Index in the raw buffer where we need to start reading to decode.
     */
    var bufferFrameStart: Int = 0

    /**
     * Local Color Table.
     */
    @ColorInt
    var localColorTable: IntArray? = null
}
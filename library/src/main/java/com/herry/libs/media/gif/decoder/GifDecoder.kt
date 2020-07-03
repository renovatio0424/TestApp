package com.herry.libs.media.gif.decoder

import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.math.min

/**
 * Created by herry.park on 2020/06/17.
 **/
/**
 * Reads frame data from a GIF image source and decodes it into individual frames for animation
 * purposes.  Image data can be read from either and InputStream source or a byte[].
 *
 *
 * This class is optimized for running animations with the frames, there are no methods to get
 * individual frame images, only to decode the next frame in the animation sequence.  Instead, it
 * lowers its memory footprint by only housing the minimum data necessary to decode the next frame
 * in the animation sequence.
 *
 *
 * The animation must be manually moved forward using [.advance] before requesting the
 * next frame.  This method must also be called before you request the first frame or an error
 * will occur.
 *
 *
 * Implementation adapted from sample code published in Lyons. (2004). *Java for
 * Programmers*, republished under the MIT Open Source License
 *
 * @see [GIF 89a Specification](https://www.w3.org/Graphics/GIF/spec-gif89a.txt)
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class GifDecoder(
    private val bitmapProvider: IGifBitmapProvider = GifBitmapProvider()
) {

    /**
     * Status codes that can be used with a GIF decoder.
     */
    enum class GifDecodeStatus {
        /**
         * File read status: No errors.
         */
        OK

        /**
         * File read status: Error decoding file (may be partially decoded).
         */
        , FORMAT_ERROR

        /**
         * File read status: Unable to open source.
         */
        , OPEN_ERROR

        /**
         * Unable to fully decode the current frame.
         */
        , PARTIAL_DECODE
    }

    companion object {
        private val TAG = GifDecoder::class.java.simpleName

        /**
         * Maximum pixel stack size for decoding LZW compressed data.
         */
        private const val MAX_STACK_SIZE = 4 * 1024
        private const val NULL_CODE = -1
        private const val INITIAL_FRAME_POINTER = -1
        private const val BYTES_PER_INTEGER = Integer.SIZE / 8
        private const val MASK_INT_LOWEST_BYTE = 0x000000FF

        @ColorInt
        private val COLOR_TRANSPARENT_BLACK = 0x00000000

        /**
         * The total iteration count which means repeat forever.
         */
        const val TOTAL_ITERATION_COUNT_FOREVER = 0
    }

    /**
     * Raw GIF data from input source.
     */
    private var rawData: ByteBuffer? = null

    var header: GifHeader? = null

    private var headerParser: GifHeaderParser? = null

    // Global File Header values and parsing flags.
    /**
     * Active color table.
     * Maximum size is 256, see GifHeaderParser.readColorTable
     */
    @ColorInt
    private var act: IntArray? = null

    /**
     * Private color table that can be modified if needed.
     */
    @ColorInt
    private val pct = IntArray(256)

    /**
     * Raw data read working array.
     */
    private var block: ByteArray? = null

    // LZW decoder working arrays.
    private var prefix: ShortArray? = null
    private var suffix: ByteArray? = null
    private var pixelStack: ByteArray? = null
    private var mainPixels: ByteArray? = null

    @ColorInt
    private var mainScratch: IntArray? = null
    private var framePointer = 0
    private var previousImage: Bitmap? = null
    private var savePrevious = false

    /**
     * Status will update per frame to allow the caller to tell whether or not the current frame
     * was decoded successfully and/or completely. Format and open failures persist across frames.
     */
    private var status: GifDecodeStatus = GifDecodeStatus.OK
    private var sampleSize = 0
    private var downSampledHeight = 0
    private var downSampledWidth = 0
    private var isFirstFrameTransparent: Boolean? = null
    private var bitmapConfig = Bitmap.Config.ARGB_8888

    /**
     * Gets gif width
     */
    fun getWidth(): Int {
        return header?.width ?: 0
    }

    /**
     * Gets gif height
     */
    fun getHeight(): Int {
        return header?.height ?: 0
    }

    fun getData(): ByteBuffer? {
        return rawData
    }

    /**
     * Move the animation frame counter forward.
     */
    fun advance() {
        header?.let { header ->
            framePointer = (framePointer + 1) % header.frameCount
        }
    }

//    @Synchronized
//    fun setData(filePath: String, @IntRange(from = 1, to = Int.MAX_VALUE.toLong()) sampleSize: Int = 1) {
//
//    }

    @Synchronized
    fun setData(data: ByteArray, @IntRange(from = 1, to = Int.MAX_VALUE.toLong()) sampleSize: Int = 1) {
        setData(ByteBuffer.wrap(data), sampleSize)
    }

    @Synchronized
    fun setData(buffer: ByteBuffer, @IntRange(from = 1, to = Int.MAX_VALUE.toLong()) sampleSize: Int = 1) {
        // Make sure sample size is a power of 2.
        val bitSampleSize = Integer.highestOneBit(sampleSize)

        status = GifDecodeStatus.OK

        headerParser = GifHeaderParser()
        headerParser?.setData(buffer)
        header = headerParser?.parseHeader()

        framePointer = INITIAL_FRAME_POINTER

        // Initialize the raw data buffer.
        rawData = buffer.asReadOnlyBuffer().apply {
            position(0)
            order(ByteOrder.LITTLE_ENDIAN)
        }

        // No point in specially saving an old frame if we're never going to use it.
        savePrevious = false
        this.header?.let { header ->
            for (frame in header.frames) {
                if (frame.dispose === GifFrame.Disposal.PREVIOUS) {
                    savePrevious = true
                    break
                }
            }

            downSampledWidth = header.width / bitSampleSize
            downSampledHeight = header.height / bitSampleSize
        } ?: run {
            downSampledWidth = 0
            downSampledHeight = 0
        }

        this.sampleSize = bitSampleSize

        // Now that we know the size, init scratch arrays.
        mainPixels = bitmapProvider.obtainByteArray((header?.width ?: 0) * (header?.height ?: 0))
        mainScratch = bitmapProvider.obtainIntArray(downSampledWidth * downSampledHeight)

        advance()
    }

    /**
     * Reads GIF image from byte array.
     *
     * @param data containing GIF file.
     * @return read status
     * @see GifDecodeStatus 
     */
    @Synchronized
    fun read(data: ByteArray?): GifDecodeStatus {
        checkNotNull(header) { "You must call setData() before read()" }

        if (data != null) {
            setData(data)
        }
        return status
    }

    /**
     * Reads GIF image from stream.
     *
     * @param inputStream containing GIF file.
     * @return read status code (0 = no errors).
     */
    @Synchronized
    fun read(inputStream: InputStream?, contentLength: Int): GifDecodeStatus {
        if (inputStream != null) {
            try {
                val capacity = if (contentLength > 0) contentLength + 4 * 1024 else 16 * 1024
                val buffer = ByteArrayOutputStream(capacity)
                var nRead: Int
                val data = ByteArray(16 * 1024)
                while (inputStream.read(data, 0, data.size).also { nRead = it } != -1) {
                    buffer.write(data, 0, nRead)
                }
                buffer.flush()
                read(buffer.toByteArray())
            } catch (e: IOException) {
                Log.w(TAG, "Error reading data from stream", e)
            }
        } else {
            status = GifDecodeStatus.OPEN_ERROR
        }

        try {
            inputStream?.close()
        } catch (e: IOException) {
            Log.w(TAG, "Error closing stream", e)
        }
        return status
    }

    fun clear() {
        header = null
        mainPixels?.let { mainPixels ->
            bitmapProvider.release(mainPixels)
        }
        mainPixels = null

        mainScratch?.let { mainScratch ->
            bitmapProvider.release(mainScratch)
        }
        mainScratch = null

        previousImage?.let { previousImage ->
            bitmapProvider.release(previousImage)
        }
        previousImage = null

        block?.let { block ->
            bitmapProvider.release(block)
        }

        rawData = null
        isFirstFrameTransparent = null
    }

    /**
     * Gets display duration for specified frame.
     *
     * @param n int index of frame.
     * @return delay in milliseconds.
     */
    fun getDelay(n: Int): Long {
        var delay: Long = -1
        header?.let { header ->
            delay = header.frames.getOrNull(n)?.delay ?: -1
        }

        return delay
    }

    /**
     * Gets total duration for all frame
     *
     * @return delay int milliseconds
     */
    fun getDuration(): Long {
        var delay: Long = 0
        header?.let { header ->
            header.frames.forEach { frame ->
                delay += frame.delay
            }
        }
        return delay
    }

    /**
     * Gets display duration for the upcoming frame in ms.
     */
    fun getNextDelay(): Long {
        header?.let { header ->
            if (header.frameCount <= 0 || framePointer < 0) {
                return@getNextDelay 0
            }
        }

        return getDelay(framePointer)
    }

    /**
     * Gets the number of frames read from file.
     *
     * @return frame count.
     */
    fun getFrameCount(): Int {
//        val headerInfo = header?.frameCount ?: 0
//        val frameSize = header?.frames?.size ?: 0
        return header?.frames?.size ?: 0
    }

    /**
     * Gets the current index of the animation frame, or -1 if animation hasn't not yet started.
     *
     * @return frame index.
     */
    fun getCurrentFrameIndex(): Int = framePointer

    /**
     * Resets the frame pointer to before the 0th frame, as if we'd never used this decoder to
     * decode any frames.
     */
    fun resetFrameIndex() {
        framePointer = INITIAL_FRAME_POINTER
    }

    /**
     * Gets the "Netscape" loop count, if any.
     * A count of 0 ([GifHeader.NETSCAPE_LOOP_COUNT_FOREVER]) means repeat indefinitely.
     * It must not be a negative value.
     * <br></br>
     * Use [.getTotalIterationCount]
     * to know how many times the animation sequence should be displayed.
     *
     * @return loop count if one was specified,
     * else -1 ([GifHeader.NETSCAPE_LOOP_COUNT_DOES_NOT_EXIST]).
     */
    fun getNetscapeLoopCount(): Int = header?.loopCount ?: -1

    /**
     * Gets the total count
     * which represents how many times the animation sequence should be displayed.
     * A count of 0 ([.TOTAL_ITERATION_COUNT_FOREVER]) means repeat indefinitely.
     * It must not be a negative value.
     *
     *
     * The total count is calculated as follows by using [.getNetscapeLoopCount].
     * This behavior is the same as most web browsers.
     * <table border='1'>
     * <tr class='tableSubHeadingColor'><th>`getNetscapeLoopCount()`</th>
     * <th>The total count</th></tr>
     * <tr><td>[GifHeader.NETSCAPE_LOOP_COUNT_FOREVER]</td>
     * <td>[.TOTAL_ITERATION_COUNT_FOREVER]</td></tr>
     * <tr><td>[GifHeader.NETSCAPE_LOOP_COUNT_DOES_NOT_EXIST]</td>
     * <td>`1`</td></tr>
     * <tr><td>`n (n > 0)`</td>
     * <td>`n + 1`</td></tr>
    </table> *
     *
     *
     * @return total iteration count calculated from "Netscape" loop count.
     * @see [Discussion about
     * the iteration count of animated GIFs
    ](https://bugs.chromium.org/p/chromium/issues/detail?id=592735.c5) */
    fun getTotalIterationCount(): Int {
        return header?.let { header ->
            when (header.loopCount) {
                GifHeader.NETSCAPE_LOOP_COUNT_DOES_NOT_EXIST -> {
                    1
                }
                GifHeader.NETSCAPE_LOOP_COUNT_FOREVER -> {
                    TOTAL_ITERATION_COUNT_FOREVER
                }
                else -> {
                    header.loopCount + 1
                }
            }
        } ?: 0
    }

    /**
     * Returns an estimated byte size for this decoder based on the data provided to [ ][.setData], as well as internal buffers.
     */
    fun getByteSize(): Int {
        return (rawData?.limit() ?: 0) + (mainPixels?.size ?: 0) + ((mainScratch?.size ?: 0) * BYTES_PER_INTEGER)
    }

    /**
     * Get the next frame in the animation sequence.
     *
     * @return Bitmap representation of frame.
     */
    @Synchronized
    fun getNextFrame(): Bitmap? {
        val header: GifHeader = header ?: return null

        if (header.frameCount <= 0 || framePointer < 0) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(
                    TAG, "Unable to decode frame"
                            + ", frameCount=" + header.frameCount
                            + ", framePointer=" + framePointer
                )
            }
            status = GifDecodeStatus.FORMAT_ERROR
        }

        if (status == GifDecodeStatus.FORMAT_ERROR || status == GifDecodeStatus.OPEN_ERROR) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Unable to decode frame, status=$status")
            }
            return null
        }

        status = GifDecodeStatus.OK
        if (block == null) {
            block = bitmapProvider.obtainByteArray(255)
        }
        val currentFrame: GifFrame = header.frames.getOrNull(framePointer) ?: run {
            status = GifDecodeStatus.FORMAT_ERROR
            return null
        }

        var previousFrame: GifFrame? = null
        val previousIndex = framePointer - 1
        if (previousIndex >= 0) {
            previousFrame = header.frames.getOrNull(previousIndex)
        }

        // Set the appropriate color table.
        act = if (currentFrame.localColorTable != null) currentFrame.localColorTable else header.gct
        if (act == null) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "No valid color table found for frame #$framePointer")
            }
            // No color table defined.
            status = GifDecodeStatus.FORMAT_ERROR
            return null
        } else {
            // Reset the transparent pixel in the color table
            if (currentFrame.transparency) {
                // Prepare local copy of color table ("pct = act"), see #1068
                System.arraycopy(act!!, 0, pct, 0, act!!.size)
                // Forget about act reference from shared header object, use copied version
                act = pct
                // Set transparent color if specified.
                act!![currentFrame.transparencyIndex] = COLOR_TRANSPARENT_BLACK
                if (currentFrame.dispose === GifFrame.Disposal.BACKGROUND && framePointer == 0) {
                    // TODO: We should check and see if all individual pixels are replaced. If they are, the
                    // first frame isn't actually transparent. For now, it's simpler and safer to assume
                    // drawing a transparent background means the GIF contains transparency.
                    isFirstFrameTransparent = true
                }
            }

            // Transfer pixel data to image.
            return setPixels(currentFrame, previousFrame)
        }
    }

    /**
     * Sets the default {@link Bitmap.Config} to use when decoding frames of a GIF.
     *
     * <p>Valid options are {@link Bitmap.Config#ARGB_8888} and
     * {@link Bitmap.Config#RGB_565}.
     * {@link Bitmap.Config#ARGB_8888} will produce higher quality frames, but will
     * also use 2x the memory of {@link Bitmap.Config#RGB_565}.
     *
     * <p>Defaults to {@link Bitmap.Config#ARGB_8888}
     *
     * <p>This value is not a guarantee. For example if set to
     * {@link Bitmap.Config#RGB_565} and the GIF contains transparent pixels,
     * {@link Bitmap.Config#ARGB_8888} will be used anyway to support the
     * transparency.
     */
    fun setDefaultBitmapConfig(config: Bitmap.Config) {
        require(!(config != Bitmap.Config.ARGB_8888 && config != Bitmap.Config.RGB_565)) {
            ("Unsupported format: " + config
                    + ", must be one of " + Bitmap.Config.ARGB_8888 + " or " + Bitmap.Config.RGB_565)
        }
        bitmapConfig = config
    }

    /**
     * Creates new frame image from current data (and previous frames as specified by their
     * disposition codes).
     */
    private fun setPixels(currentFrame: GifFrame, previousFrame: GifFrame?): Bitmap? {
        val header: GifHeader = this.header ?: return null
 
        // Final location of blended pixels.
        val dest: IntArray = mainScratch ?: return null

        // clear all pixels when meet first frame and drop prev image from last loop
        if (previousFrame == null) {
            previousImage?.let { previousImage ->
                bitmapProvider.release(previousImage)
            }
            previousImage = null
            Arrays.fill(dest, COLOR_TRANSPARENT_BLACK)
        }

        // clear all pixels when dispose is 3 but previousImage is null.
        // When PREVIOUS and previousImage didn't be set, new frame should draw on
        // a empty image
        if (previousFrame != null && previousFrame.dispose === GifFrame.Disposal.PREVIOUS && previousImage == null
        ) {
            Arrays.fill(dest, COLOR_TRANSPARENT_BLACK)
        }

        // fill in starting image contents based on last image's dispose code
        if (previousFrame != null && previousFrame.dispose.value > GifFrame.Disposal.UNSPECIFIED.value) {
            // We don't need to do anything for NONE, if it has the correct pixels so will our
            // mainScratch and therefore so will our dest array.
            if (previousFrame.dispose === GifFrame.Disposal.BACKGROUND) {
                // Start with a canvas filled with the background color
                @ColorInt var color = COLOR_TRANSPARENT_BLACK
                if (!currentFrame.transparency) {
                    color = header.backgroundColor
                    if (currentFrame.localColorTable != null && header.backgroundColorIndex == currentFrame.transparencyIndex) {
                        color = COLOR_TRANSPARENT_BLACK
                    }
                }
                // The area used by the graphic must be restored to the background color.
                val downSampledIH = previousFrame.imageHeight / sampleSize
                val downSampledIY = previousFrame.imageYPosition / sampleSize
                val downSampledIW = previousFrame.imageWidth / sampleSize
                val downSampledIX = previousFrame.imageXPosition / sampleSize
                val topLeft = downSampledIY * downSampledWidth + downSampledIX
                val bottomLeft = topLeft + downSampledIH * downSampledWidth
                var left = topLeft
                while (left < bottomLeft) {
                    val right = left + downSampledIW
                    for (pointer in left until right) {
                        dest[pointer] = color
                    }
                    left += downSampledWidth
                }
            } else if (previousFrame.dispose === GifFrame.Disposal.PREVIOUS && previousImage != null) {
                // Start with the previous frame
                previousImage!!.getPixels(
                    dest, 0, downSampledWidth, 0, 0, downSampledWidth,
                    downSampledHeight
                )
            }
        }

        // Decode pixels for this frame into the global pixels[] scratch.
        decodeBitmapData(currentFrame)
        if (currentFrame.interlace || sampleSize != 1) {
            copyCopyIntoScratchRobust(currentFrame)
        } else {
            copyIntoScratchFast(currentFrame)
        }

        // Copy pixels into previous image
        if (savePrevious && (currentFrame.dispose === GifFrame.Disposal.UNSPECIFIED
                    || currentFrame.dispose === GifFrame.Disposal.NONE)
        ) {
            if (previousImage == null) {
                previousImage = getNextBitmap()
            }
            previousImage?.setPixels(
                dest, 0, downSampledWidth, 0, 0, downSampledWidth,
                downSampledHeight
            )
        }

        // Set pixels for current image.
        return getNextBitmap().apply {
            setPixels(dest, 0, downSampledWidth, 0, 0, downSampledWidth, downSampledHeight)
        }
    }

    private fun copyIntoScratchFast(currentFrame: GifFrame) {
        val dest = mainScratch ?: return
        val downSampledIH = currentFrame.imageHeight
        val downSampledIY = currentFrame.imageYPosition
        val downSampledIW = currentFrame.imageWidth
        val downSampledIX = currentFrame.imageXPosition
        // Copy each source line to the appropriate place in the destination.
        val isFirstFrame = framePointer == 0
        val width = downSampledWidth
        val mainPixels = mainPixels ?: return
        val act = act ?: return
        var transparentColorIndex: Byte = -1
        for (i in 0 until downSampledIH) {
            val line = i + downSampledIY
            val k = line * width
            // Start of line in dest.
            var dx = k + downSampledIX
            // End of dest line.
            var dlim = dx + downSampledIW
            if (k + width < dlim) {
                // Past dest edge.
                dlim = k + width
            }
            // Start of line in source.
            var sx = i * currentFrame.imageWidth
            while (dx < dlim) {
                val byteCurrentColorIndex = mainPixels[sx]
                val currentColorIndex = byteCurrentColorIndex.toInt() and MASK_INT_LOWEST_BYTE
                if (currentColorIndex != transparentColorIndex.toInt()) {
                    val color = act[currentColorIndex]
                    if (color != COLOR_TRANSPARENT_BLACK) {
                        dest[dx] = color
                    } else {
                        transparentColorIndex = byteCurrentColorIndex
                    }
                }
                ++sx
                ++dx
            }
        }
        isFirstFrameTransparent =
            ((isFirstFrameTransparent != null) && (isFirstFrameTransparent == true))
                || ((isFirstFrameTransparent == null) && isFirstFrame && (transparentColorIndex.toInt() != -1))
    }

    private fun copyCopyIntoScratchRobust(currentFrame: GifFrame) {
        val dest = mainScratch ?: return

        val downSampledIH = currentFrame.imageHeight / sampleSize
        val downSampledIY = currentFrame.imageYPosition / sampleSize
        val downSampledIW = currentFrame.imageWidth / sampleSize
        val downSampledIX = currentFrame.imageXPosition / sampleSize
        // Copy each source line to the appropriate place in the destination.
        var pass = 1
        var inc = 8
        var iline = 0
        val isFirstFrame = framePointer == 0
        val sampleSize = sampleSize
        val downSampledWidth = downSampledWidth
        val downSampledHeight = downSampledHeight
        val mainPixels = mainPixels  ?: return
        val act = act ?: return
        var isFirstFrameTransparent = isFirstFrameTransparent
        for (i in 0 until downSampledIH) {
            var line = i
            if (currentFrame.interlace) {
                if (iline >= downSampledIH) {
                    pass++
                    when (pass) {
                        2 -> iline = 4
                        3 -> {
                            iline = 2
                            inc = 4
                        }
                        4 -> {
                            iline = 1
                            inc = 2
                        }
                        else -> {
                        }
                    }
                }
                line = iline
                iline += inc
            }
            line += downSampledIY
            val isNotDownSampling = sampleSize == 1
            if (line < downSampledHeight) {
                val k = line * downSampledWidth
                // Start of line in dest.
                var dx = k + downSampledIX
                // End of dest line.
                var dlim = dx + downSampledIW
                if (k + downSampledWidth < dlim) {
                    // Past dest edge.
                    dlim = k + downSampledWidth
                }
                // Start of line in source.
                var sx = i * sampleSize * currentFrame.imageWidth
                if (isNotDownSampling) {
                    var averageColor: Int
                    while (dx < dlim) {
                        val currentColorIndex = mainPixels[sx].toInt() and MASK_INT_LOWEST_BYTE
                        averageColor = act[currentColorIndex]
                        if (averageColor != COLOR_TRANSPARENT_BLACK) {
                            dest[dx] = averageColor
                        } else if (isFirstFrame && isFirstFrameTransparent == null) {
                            isFirstFrameTransparent = true
                        }
                        sx += sampleSize
                        dx++
                    }
                } else {
                    var averageColor: Int
                    val maxPositionInSource = sx + (dlim - dx) * sampleSize
                    while (dx < dlim) {
                        // Map color and insert in destination.
                        // TODO: This is substantially slower (up to 50ms per frame) than just grabbing the
                        // current color index above, even with a sample size of 1.
                        averageColor = averageColorsNear(sx, maxPositionInSource, currentFrame.imageWidth)
                        if (averageColor != COLOR_TRANSPARENT_BLACK) {
                            dest[dx] = averageColor
                        } else if (isFirstFrame && isFirstFrameTransparent == null) {
                            isFirstFrameTransparent = true
                        }
                        sx += sampleSize
                        dx++
                    }
                }
            }
        }
        if (this.isFirstFrameTransparent == null) {
            this.isFirstFrameTransparent = isFirstFrameTransparent ?: false
        }
    }

    @ColorInt
    private fun averageColorsNear(
        positionInMainPixels: Int, maxPositionInMainPixels: Int,
        currentFrameIw: Int
    ): Int {
        val mainPixels = mainPixels ?: return COLOR_TRANSPARENT_BLACK
        val act = act ?: return COLOR_TRANSPARENT_BLACK

        var alphaSum = 0
        var redSum = 0
        var greenSum = 0
        var blueSum = 0
        var totalAdded = 0

        // Find the pixels in the current row.
        run {
            var i = positionInMainPixels
            while (i < positionInMainPixels + sampleSize && i < mainPixels.size && i < maxPositionInMainPixels
            ) {
                val currentColorIndex = mainPixels[i].toInt() and MASK_INT_LOWEST_BYTE
                val currentColor = act[currentColorIndex]
                if (currentColor != 0) {
                    alphaSum += currentColor shr 24 and MASK_INT_LOWEST_BYTE
                    redSum += currentColor shr 16 and MASK_INT_LOWEST_BYTE
                    greenSum += currentColor shr 8 and MASK_INT_LOWEST_BYTE
                    blueSum += currentColor and MASK_INT_LOWEST_BYTE
                    totalAdded++
                }
                i++
            }
        }

        // Find the pixels in the next row.
        var i = positionInMainPixels + currentFrameIw
        while (i < positionInMainPixels + currentFrameIw + sampleSize && i < mainPixels.size && i < maxPositionInMainPixels
        ) {
            val currentColorIndex = mainPixels[i].toInt() and MASK_INT_LOWEST_BYTE
            val currentColor = act[currentColorIndex]
            if (currentColor != 0) {
                alphaSum += currentColor shr 24 and MASK_INT_LOWEST_BYTE
                redSum += currentColor shr 16 and MASK_INT_LOWEST_BYTE
                greenSum += currentColor shr 8 and MASK_INT_LOWEST_BYTE
                blueSum += currentColor and MASK_INT_LOWEST_BYTE
                totalAdded++
            }
            i++
        }

        return if (totalAdded == 0) {
            COLOR_TRANSPARENT_BLACK
        } else {
            ((alphaSum / totalAdded) shl 24) or ((redSum / totalAdded) shl 16) or ((greenSum / totalAdded) shl 8) or (blueSum / totalAdded)
        }
    }

    /**
     * Decodes LZW image data into pixel array. Adapted from John Cristy's BitmapMagick.
     */
    private fun decodeBitmapData(frame: GifFrame?) {
        val rawData = this.rawData ?: return
        val header = this.header ?: return
        val block = this.block ?: return

        if (frame != null) {
            // Jump to the frame start position.
            rawData.position(frame.bufferFrameStart)
        }
        val npix = if (frame == null) header.width * header.height else frame.imageWidth * frame.imageHeight
        var available: Int
        val clear: Int
        var codeMask: Int
        var codeSize: Int
        val endOfInformation: Int
        var inCode: Int
        var oldCode: Int
        var bits: Int
        var code: Int
        var count: Int
        var i: Int
        var datum: Int
        var first: Int
        var top: Int
        var bi: Int
        var pi: Int
        if (mainPixels == null || mainPixels!!.size < npix) {
            // Allocate new pixel array.
            mainPixels = bitmapProvider.obtainByteArray(npix)
        }
        val mainPixels = mainPixels ?: return
        if (prefix == null) {
            prefix = ShortArray(MAX_STACK_SIZE)
        }
        val prefix = prefix
        if (suffix == null) {
            suffix = ByteArray(MAX_STACK_SIZE)
        }
        val suffix = suffix
        if (pixelStack == null) {
            pixelStack = ByteArray(MAX_STACK_SIZE + 1)
        }
        val pixelStack = pixelStack

        // Initialize GIF data stream decoder.
        val dataSize: Int = readByte()
        clear = 1 shl dataSize
        endOfInformation = clear + 1
        available = clear + 2
        oldCode = NULL_CODE
        codeSize = dataSize + 1
        codeMask = (1 shl codeSize) - 1
        code = 0
        while (code < clear) {

            // XXX ArrayIndexOutOfBoundsException.
            prefix!![code] = 0
            suffix!![code] = code.toByte()
            code++
        }

        // Decode GIF pixel stream.
        bi = 0
        pi = bi
        top = pi
        first = top
        count = first
        bits = count
        datum = bits
        i = datum
        while (i < npix) {
            // Read a new data block.
            if (count == 0) {
                count = readBlock()
                if (count <= 0) {
                    status = GifDecodeStatus.PARTIAL_DECODE
                    break
                }
                bi = 0
            }
            datum += block[bi].toInt() and MASK_INT_LOWEST_BYTE shl bits
            bits += 8
            ++bi
            --count
            while (bits >= codeSize) {
                // Get the next code.
                code = datum and codeMask
                datum = datum shr codeSize
                bits -= codeSize

                // Interpret the code.
                if (code == clear) {
                    // Reset decoder.
                    codeSize = dataSize + 1
                    codeMask = (1 shl codeSize) - 1
                    available = clear + 2
                    oldCode = NULL_CODE
                    continue
                } else if (code == endOfInformation) {
                    break
                } else if (oldCode == NULL_CODE) {
                    mainPixels[pi] = suffix!![code]
                    ++pi
                    ++i
                    oldCode = code
                    first = code
                    continue
                }
                inCode = code
                if (code >= available) {
                    pixelStack!![top] = first.toByte()
                    ++top
                    code = oldCode
                }
                while (code >= clear) {
                    pixelStack!![top] = suffix!![code]
                    ++top
                    code = prefix!![code].toInt()
                }
                first = suffix!![code].toInt() and MASK_INT_LOWEST_BYTE
                mainPixels[pi] = first.toByte()
                ++pi
                ++i
                while (top > 0) {
                    // Pop a pixel off the pixel stack.
                    mainPixels[pi] = pixelStack!![--top]
                    ++pi
                    ++i
                }

                // Add a new string to the string table.
                if (available < MAX_STACK_SIZE) {
                    prefix!![available] = oldCode.toShort()
                    suffix[available] = first.toByte()
                    ++available
                    if (available and codeMask == 0 && available < MAX_STACK_SIZE) {
                        ++codeSize
                        codeMask += available
                    }
                }
                oldCode = inCode
            }
        }

        // Clear missing pixels.
        Arrays.fill(mainPixels, pi, npix, COLOR_TRANSPARENT_BLACK.toByte())
    }

    /**
     * Reads a single byte from the input stream.
     */
    private fun readByte(): Int {
        return if (null == rawData) 0 else (rawData!!.get().toInt() and MASK_INT_LOWEST_BYTE)
    }

    /**
     * Reads next variable length block from input.
     *
     * @return number of bytes stored in "buffer".
     */
    private fun readBlock(): Int {
        val blockSize = readByte()
        if (blockSize <= 0) {
            return blockSize
        }
        rawData?.let { rawData ->
            block?.let { block ->
                rawData[block, 0, min(blockSize, rawData.remaining())]
            }
        }
        return blockSize
    }
    
    private fun getNextBitmap(): Bitmap {
        val config = if (isFirstFrameTransparent == null || isFirstFrameTransparent == true) {
            Bitmap.Config.ARGB_8888
        } else {
            bitmapConfig
        }
        val result = bitmapProvider.obtain(downSampledWidth, downSampledHeight, config)
        result.setHasAlpha(true)
        return result
    }
}

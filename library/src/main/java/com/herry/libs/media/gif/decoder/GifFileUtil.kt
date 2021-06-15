package com.herry.libs.media.gif.decoder

import java.io.*

internal object GifFileUtil {

    @Throws(IOException::class)
    fun readFileToByteArray(file: File): ByteArray? {
        val fileInputStream = openInputStream(file)
        val fileLength = file.length()
        // file.length() may return 0 for system-dependent entities, treat 0 as unknown length - see IO-453
        return if (fileLength > 0) toByteArray(
            fileInputStream,
            fileLength.toInt()
        ) else toByteArray(fileInputStream)
    }

    @Throws(Exception::class)
    fun openInputStream(file: File): FileInputStream {
        if (file.exists()) {
            if (file.isDirectory) {
                throw IOException("File '$file' exists but is a directory")
            }
            if (!file.canRead()) {
                throw IOException("File '$file' cannot be read")
            }
        } else {
            throw FileNotFoundException("File '$file' does not exist")
        }
        return FileInputStream(file)
    }

    @Throws(Exception::class)
    fun toByteArray(input: InputStream, size: Int): ByteArray {
        require(size >= 0) { "Size must be equal or greater than zero: $size" }
        if (size == 0) {
            return ByteArray(0)
        }
        try {
            val data = ByteArray(size)
            var offset = 0
            var read = 0
            while (offset < size && input.read(data, offset, size - offset)
                    .also { read = it } != -1
            ) {
                offset += read
            }
            if (offset != size) {
                throw IOException("Unexpected read size. current: $offset, expected: $size")
            }
            return data
        } catch (err: OutOfMemoryError) {
            throw Exception(Throwable(err.localizedMessage))
        }
    }

    @Throws(IOException::class)
    fun toByteArray(input: InputStream?): ByteArray? {
        input ?: return null
        ByteArrayOutputStream().use { output ->
            copy(input, output)
            return output.toByteArray()
        }
    }

    @Throws(IOException::class)
    fun copy(
        input: InputStream,
        output: OutputStream
    ): Int {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var count = 0
        var n: Int
        while (-1 != input.read(buffer).also { n = it }) {
            output.write(buffer, 0, n)
            count += n
        }
        return count
    }
}

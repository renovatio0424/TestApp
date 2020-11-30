package com.herry.libs.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import java.io.*
import java.nio.channels.FileChannel


/**
 * Created by herry.park on 2020/06/18.
 **/
@Suppress("unused")
object FileUtil {

    /**
     * Reads the contents of a file into a byte array.
     * The file is always closed.
     *
     * @param file the file to read, must not be `null`
     * @return the file contents, never `null`
     * @throws IOException in case of an I/O error
     * @since 1.1
     */
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

    /**
     * Opens a [FileInputStream] for the specified file, providing better
     * error messages than simply calling `new FileInputStream(file)`.
     *
     *
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     *
     *
     *
     * An exception is thrown if the file does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be read.
     *
     *
     * @param file the file to open for input, must not be `null`
     * @return a new [FileInputStream] for the specified file
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException           if the file object is a directory
     * @throws IOException           if the file cannot be read
     * @since 1.3
     */
    @Throws(IOException::class)
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

    private fun deleteDirectory(directory: File?): Boolean {
        if (null != directory) {
            if (directory.isDirectory) {
                val children = directory.list()
                if (null != children) {
                    for (child in children) {
                        val success =
                            deleteDirectory(File(directory, child))
                        if (!success) {
                            return false
                        }
                    }
                }
            }
            return directory.delete()
        }
        return false
    }

    @Throws(IOException::class)
    fun copyFileOrDirectory(srcDir: String?, dstDir: String?) {
        if (srcDir.isNullOrEmpty() || dstDir.isNullOrEmpty()) {
            throw IOException()
        }

        try {
            val src = File(srcDir)
            val dst = File(dstDir, src.name)
            if (src.isDirectory) {
                val files = src.list()
                if (null != files) {
//                    val filesLength = files.size
                    for (file in files) {
                        if (TextUtils.isEmpty(file)) {
                            continue
                        }
                        val src1 = File(src, file).path
                        val dst1 = dst.path
                        copyFileOrDirectory(src1, dst1)
                    }
                }
            } else {
                copyFile(src, dst)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun copyFile(sourceFile: File, destFile: File) {
        if (destFile.parentFile?.exists() != true) {
            destFile.parentFile?.mkdirs()
        }

        if (!destFile.exists()) {
            destFile.createNewFile()
        }

        var source: FileChannel? = null
        var destination: FileChannel? = null

        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination.transferFrom(source, 0, source.size())
        } finally {
            source?.close()
            destination?.close()
        }
    }

    // IO -----------------------------------------------------------------------------------------

    /**
     * Gets the contents of an `InputStream` as a `byte[]`.
     * Use this method instead of `toByteArray(InputStream)`
     * when `InputStream` size is known
     *
     * @param input the `InputStream` to read from
     * @param size the size of `InputStream`
     * @return the requested byte array
     * @throws IOException              if an I/O error occurs or `InputStream` size differ from parameter
     * size
     * @throws IllegalArgumentException if size is less than zero
     * @since 2.1
     */
    @Throws(IOException::class)
    fun toByteArray(input: InputStream, size: Int): ByteArray {
        require(size >= 0) { "Size must be equal or greater than zero: $size" }
        if (size == 0) {
            return ByteArray(0)
        }
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
    }

    /**
     * Gets the contents of an `InputStream` as a `byte[]`.
     *
     *
     * This method buffers the input internally, so there is no need to use a
     * `BufferedInputStream`.
     *
     * @param input the `InputStream` to read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    @Throws(IOException::class)
    private fun toByteArray(input: InputStream?): ByteArray? {
        input ?: return null
        ByteArrayOutputStream().use { output ->
            copy(input, output)
            return output.toByteArray()
        }
    }

    /**
     * Copy bytes from an `InputStream` to an`OutputStream`.
     * @param input the `InputStream` to read from
     * @param output the `OutputStream` to write to
     * @return the number of bytes copied
     * @throws IOException In case of an I/O problem
     */
    @Throws(IOException::class)
    private fun copy(
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

    /**
     * Gets path from Uri
     */
    fun getUriPath(context: Context?, uri: Uri?) : String {
        context ?: return ""
        uri ?: return ""

        if (uri.toString().startsWith("content://")) {
            @Suppress("DEPRECATION") val projection = arrayOf(MediaStore.MediaColumns.DATA)
            val cursor: Cursor? = context.contentResolver?.query(uri, projection, null, null, null)
            cursor?.use { _cursor ->
                if (_cursor.moveToFirst()) {
                    return _cursor.getString(0)
                }
            }
        }

        return uri.path ?: ""
    }
}
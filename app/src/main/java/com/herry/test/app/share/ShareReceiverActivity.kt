package com.herry.test.app.share

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.TextView
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.herry.test.R
import com.herry.test.app.base.BaseActivity
import java.io.File

class ShareReceiverActivity: BaseActivity() {

    private var textView: TextView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.share_receiver_activity)

        var sourceUri: Uri? = null
        var exportUri: Uri? = null

        intent?.let { intent ->
            val sourceUriString = intent.getStringExtra("sourceUri") ?: ""
            if (sourceUriString.isNotEmpty()) {
                sourceUri = Uri.parse(sourceUriString)
            }

            val exportUriString = intent.getStringExtra("exportUri") ?: ""
            if (exportUriString.isNotEmpty()) {
                exportUri = Uri.parse(exportUriString)
            }
        }

        var sourceUriSize = 0
        var sourceUriNameAndSize: Pair<String, Long>? = null
        sourceUri?.let { uri ->
            sourceUriNameAndSize = getUriFileNameAndSize(uri)

            contentResolver.openInputStream(uri)?.use { inputStream ->
                val buffer = ByteArray(1024)
                var readSize: Int
                while (inputStream.read(buffer, 0, buffer.size).also { readSize = it } != -1) {
                    sourceUriSize += readSize
                }
            }
        }
        val sourceRealPath = getRealPathFromUri(sourceUri)
        val sourceUriFile: File? = if (sourceRealPath.isNotEmpty()) File(sourceRealPath) else null

        var exportUriSize = 0
        var exportUriNameAndSize: Pair<String, Long>? = null
        exportUri?.let { uri ->
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                val fileContents = "Hello world!"
                outputStream.write(fileContents.toByteArray())
            }
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val buffer = ByteArray(1024)
                var readSize: Int
                while (inputStream.read(buffer, 0, buffer.size).also { readSize = it } != -1) {
                    exportUriSize += readSize
                }
            }

            exportUriNameAndSize = getUriFileNameAndSize(uri)
        }
        val exportUriRealPath = getRealPathFromUri(exportUri)
        val exportUriFile: File? = if (exportUriRealPath.isNotEmpty()) File(exportUriRealPath) else null

        textView = findViewById(R.id.share_receiver_activity_text)
        textView?.text = "sourceUri: ${getUriParseString(sourceUri)}" +
                "\nfile name: ${sourceUriNameAndSize?.first ?: ""}" +
                "\nfile size: ${sourceUriNameAndSize?.second ?: ""}, read size: $sourceUriSize" +
                "\nreal path: ${sourceUriFile?.absoluteFile ?: ""}, access enable: ${sourceUriFile?.canRead() ?: false}" +
                "\nexportUri: ${getUriParseString(exportUri)}" +
                "\nfile name: ${exportUriNameAndSize?.first ?: ""}" +
                "\nfile size: ${exportUriNameAndSize?.second ?: ""}, read size: $exportUriSize" +
                "\nreal path: ${exportUriFile?.absoluteFile ?: ""}, access enable: ${exportUriFile?.canRead() ?: false}"
    }

    private fun getUriParseString(uri: Uri?): String {
        uri ?: return ""

        return "$uri"+
                "\n\tauthority: ${uri.authority}" +
                "\n\tpath: ${uri.path}"
    }

    private fun getUriFileNameAndSize(uri: Uri?): Pair<String, Long> {
        var path = ""
        var size = 0L
        uri?.let { returnUri ->
            contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()

            path = cursor.getStringOrNull(nameIndex) ?: ""
            size = cursor.getLongOrNull(sizeIndex) ?: 0L
        }

        return Pair(path, size)
    }

    private fun getRealPathFromUri(uri: Uri?): String {
        var path = ""

        if (uri != null) {
            if (uri.authority == "media") { // media store uri
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    cursor.moveToFirst()
                    @Suppress("DEPRECATION")
                    path = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)) ?: ""
                }
            } else if (uri.authority == "com.nexstreaming.app.kinemasterfree") {
                path = uri.path ?: ""
            }
        }

        return path
    }
}
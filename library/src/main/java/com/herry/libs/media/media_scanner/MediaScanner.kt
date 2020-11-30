package com.herry.libs.media.media_scanner

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri

class MediaScanner(private val context: Context) {
    private var path: String? = null
    private var mediaScanner: MediaScannerConnection? = null
    private var mediaScannerClient: MediaScannerConnection.MediaScannerConnectionClient? = null

    companion object {
        fun newInstance(context: Context): MediaScanner {
            return MediaScanner(context);
        }
    }

    fun mediaScanning(path: String) {
        if (mediaScanner == null) {
            mediaScannerClient = object: MediaScannerConnection.MediaScannerConnectionClient {
                override fun onScanCompleted(path: String?, uri: Uri?) {
                    mediaScanner?.disconnect();
                }

                override fun onMediaScannerConnected() {
                    mediaScanner?.scanFile(path, null)
                }
            };
            mediaScanner = MediaScannerConnection(context, mediaScannerClient)
        }
        this.path = path
        mediaScanner?.connect()
    }
}
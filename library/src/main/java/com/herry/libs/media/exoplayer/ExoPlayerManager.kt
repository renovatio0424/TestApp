package com.herry.libs.media.exoplayer

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

@Suppress("unused")
class ExoPlayerManager(private val context: () -> Context?, private val isSingleInstance: Boolean = false) {
    private val tag = "ExoPlayerManager"
    private var currentRequestedPlayingId: String? = null
    private val playerMap = HashMap<String, ExoPlayer>()

    private fun internalPrepare(id: String, url: String, fromPlay: Boolean): ExoPlayer? {
        val context = context.invoke() ?: return null

        Log.d(tag, "prepare for $id")

        var player = playerMap[id]
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
            playerMap[id] = player
        }

        Log.d(tag, "prepare player status ${player.playbackState}")

        if (player.playbackState == ExoPlayer.STATE_IDLE) {
            player.setMediaSource(
                ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                    .createMediaSource(MediaItem.fromUri(url)))

            if (isSingleInstance) {
                if (fromPlay) {
                    player.prepare()
                }
            } else {
                player.prepare()
            }
        }

        return player
    }

    fun prepare(id: String, url: String): ExoPlayer? {
        return internalPrepare(id, url, false)
    }

    fun getPlayer(id: String): ExoPlayer? = playerMap[id]

    fun play(id: String, url: String, repeat: Boolean = false) {
        Log.d(tag, "play for $id")

        var player = playerMap[id]
        if (player == null) {
            player = internalPrepare(id, url, true) ?: return
            player.repeatMode = if (repeat) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
            player.playWhenReady = true
        } else {
            player.repeatMode = if (repeat) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
            if (isSingleInstance) {
                player.prepare()
                player.playWhenReady = true
            } else {
                player.play()
            }
        }
    }

    fun stop(id: String) {
        Log.d(tag, "stop for $id")

        val player = playerMap[id]
        player?.let { exoPlayer ->
            exoPlayer.stop()
            exoPlayer.release()

            playerMap.remove(id)
        }
    }

    fun stopAll() {
        currentRequestedPlayingId = null
        playerMap.values.forEach { exoPlayer ->
            exoPlayer.stop()
            exoPlayer.release()
        }
        playerMap.clear()
    }
}
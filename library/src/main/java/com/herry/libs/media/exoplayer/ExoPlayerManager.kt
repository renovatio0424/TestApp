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
    private val playerMap = HashMap<String, ExoPlayer>()

    private fun internalPrepare(id: String, url: String, fromPlay: Boolean): ExoPlayer? {
        val context = context.invoke() ?: return null

        var player = playerMap[id]
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
            playerMap[id] = player
        }

        if (player.playbackState == ExoPlayer.STATE_IDLE) {
            player.setMediaSource(
                ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                    .createMediaSource(MediaItem.fromUri(url))
            )

            if (isSingleInstance) {
                if (fromPlay) {
                    Log.d(tag, "[$tag] prepare for $id")
                    player.prepare()
                }
            } else {
                Log.d(tag, "[$tag] prepare for $id")

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
        var player = playerMap[id]
        if (player == null) {
            player = internalPrepare(id, url, true) ?: return
            player.repeatMode = if (repeat) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
            player.playWhenReady = true
        } else {
            player.repeatMode = if (repeat) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
            if (isSingleInstance) {
                Log.d(tag, "[$tag] prepare for $id")
                player.prepare()
                player.playWhenReady = true
            } else {
                Log.d(tag, "[$tag] play for $id")
                player.play()
            }
        }
    }

    fun stop(id: String) {
        val player = playerMap[id]
        player?.let { exoPlayer ->
            exoPlayer.stop()
            exoPlayer.release()

            playerMap.remove(id)

            Log.d(tag, "[$tag] stopped for $id, player total counts = ${playerMap.size}")
        }
    }

    fun stopAll() {
        Log.d(tag, "[$tag] stop all (${playerMap.size})")
        playerMap.forEach {
            Log.d(tag, "[$tag] stop all stop id = ${it.key}")
        }
        playerMap.values.forEach { exoPlayer ->
            exoPlayer.stop()
            exoPlayer.release()
        }
        playerMap.clear()
    }

    fun isPlaying(id: String): Boolean {
        val player = playerMap[id] ?: return false
        return player.playbackState == ExoPlayer.STATE_READY && player.isPlaying
    }

    fun isReadyToPlay(id: String): Boolean {
        val player = playerMap[id] ?: return false
        return player.playbackState == ExoPlayer.STATE_READY
    }

    fun pause(id: String) {
        playerMap[id]?.pause()
    }

    fun resume(id: String) {
        playerMap[id]?.play()
    }
}
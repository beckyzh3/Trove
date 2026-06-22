package com.example.trove.data

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

object VoicePlayerHelper {
    private var player: MediaPlayer? = null
    private var playingUrl: String? = null

    fun play(
        context: Context,
        url: String,
        onComplete: () -> Unit = {}
    ) {
        if (playingUrl == url && player?.isPlaying == true) {
            stop()
            return
        }

        stop()
        val mediaPlayer = MediaPlayer()
        player = mediaPlayer
        playingUrl = url

        mediaPlayer.setOnCompletionListener {
            onComplete()
            stop()
        }
        mediaPlayer.setOnErrorListener { _, _, _ ->
            stop()
            true
        }

        if (url.startsWith("http://") || url.startsWith("https://")) {
            mediaPlayer.setDataSource(url)
        } else {
            mediaPlayer.setDataSource(context, Uri.parse(url))
        }
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    fun stop() {
        runCatching { player?.stop() }
        runCatching { player?.release() }
        player = null
        playingUrl = null
    }

    fun isPlaying(url: String): Boolean =
        playingUrl == url && player?.isPlaying == true
}

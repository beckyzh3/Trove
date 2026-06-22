package com.example.trove.data

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

object VoiceRecorderHelper {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var startedAtMs: Long = 0L

    fun start(context: Context): File {
        stopRecording()
        val dir = File(context.cacheDir, "voice_memos").apply { mkdirs() }
        val file = File(dir, "memo-${UUID.randomUUID()}.m4a")
        outputFile = file
        startedAtMs = System.currentTimeMillis()

        recorder = createRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        return file
    }

    fun stopRecording(): RecordingResult? {
        val file = outputFile ?: return null
        val durationMs = if (startedAtMs > 0L) {
            System.currentTimeMillis() - startedAtMs
        } else {
            0L
        }

        runCatching {
            recorder?.stop()
        }
        runCatching {
            recorder?.release()
        }
        recorder = null
        outputFile = null
        startedAtMs = 0L

        return RecordingResult(file = file, durationMs = durationMs)
    }

    fun isRecording(): Boolean = recorder != null

    fun fileUri(context: Context, file: File): Uri =
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

    fun formatDuration(durationMs: Long): String {
        val totalSec = (durationMs / 1000).coerceAtLeast(0)
        val min = totalSec / 60
        val sec = totalSec % 60
        return "$min:${sec.toString().padStart(2, '0')}"
    }

    private fun createRecorder(context: Context): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    data class RecordingResult(
        val file: File,
        val durationMs: Long
    )
}

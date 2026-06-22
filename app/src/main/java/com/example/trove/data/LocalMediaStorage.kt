package com.example.trove.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import java.io.File
import java.util.UUID

/** Persists photos and voice memos to app internal storage (no cloud). */
object LocalMediaStorage {
    private const val MEDIA_ROOT = "trip_media"

    fun persistPhoto(
        context: Context,
        sourceUriString: String,
        tripId: String,
        fileName: String
    ): String = persistMedia(context, sourceUriString, tripId, "photos", fileName)

    fun persistVoice(
        context: Context,
        sourceUriString: String,
        tripId: String,
        fileName: String
    ): String = persistMedia(context, sourceUriString, tripId, "voice", fileName)

    fun copyAssetPhoto(
        context: Context,
        assetPath: String,
        tripId: String,
        fileName: String,
        overwrite: Boolean = false
    ): String {
        val destDir = mediaDir(context, tripId, "photos")
        val dest = File(destDir, fileName)
        if (overwrite || !dest.exists() || dest.length() == 0L) {
            context.assets.open(assetPath).use { input ->
                dest.outputStream().use { output -> input.copyTo(output) }
            }
        }
        return dest.toUri().toString()
    }

    fun copyAssetVoice(
        context: Context,
        assetPath: String,
        tripId: String,
        fileName: String,
        overwrite: Boolean = false
    ): String {
        val destDir = mediaDir(context, tripId, "voice")
        val dest = File(destDir, fileName)
        if (overwrite || !dest.exists() || dest.length() == 0L) {
            context.assets.open(assetPath).use { input ->
                dest.outputStream().use { output -> input.copyTo(output) }
            }
        }
        return dest.toUri().toString()
    }

    /** Coil model for bundled assets, local files, content URIs, and remote URLs. */
    fun imageLoaderData(url: String): Any {
        return when {
            url.startsWith("asset://") -> {
                android.net.Uri.parse("file:///android_asset/${url.removePrefix("asset://")}")
            }
            url.startsWith("file://") -> File(checkNotNull(url.toUri().path))
            url.startsWith("content://") -> url.toUri()
            else -> url
        }
    }

    fun uniquePhotoName(prefix: String = "photo"): String =
        "$prefix-${UUID.randomUUID()}.jpg"

    fun uniqueVoiceName(prefix: String = "voice"): String =
        "$prefix-${UUID.randomUUID()}.m4a"

    fun fileExists(uriString: String): Boolean {
        if (uriString.isBlank()) return false
        return runCatching {
            when {
                uriString.startsWith("file://") -> File(requireNotNull(uriString.toUri().path)).exists()
                else -> false
            }
        }.getOrDefault(false)
    }

    private fun persistMedia(
        context: Context,
        sourceUriString: String,
        tripId: String,
        subdir: String,
        fileName: String
    ): String {
        if (sourceUriString.isBlank()) return sourceUriString
        if (sourceUriString.isAssetMediaUri()) return sourceUriString
        if (!sourceUriString.isLocalMediaUri()) return sourceUriString

        val destDir = mediaDir(context, tripId, subdir)
        val dest = File(destDir, fileName)

        if (sourceUriString.startsWith("file://")) {
            val sourceFile = File(requireNotNull(sourceUriString.toUri().path))
            if (sourceFile.canonicalPath == dest.canonicalPath) return sourceUriString
            if (sourceFile.parentFile?.canonicalPath == destDir.canonicalPath && sourceFile.exists()) {
                return sourceUriString
            }
        }

        context.contentResolver.openInputStream(sourceUriString.toUri()).use { input ->
            requireNotNull(input) { "Could not read media from device" }
            dest.outputStream().use { output -> input.copyTo(output) }
        }
        return dest.toUri().toString()
    }

    private fun mediaDir(context: Context, tripId: String, subdir: String): File =
        File(context.filesDir, "$MEDIA_ROOT/$tripId/$subdir").apply { mkdirs() }
}

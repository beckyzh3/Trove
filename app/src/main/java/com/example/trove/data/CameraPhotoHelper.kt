package com.example.trove.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

object CameraPhotoHelper {
    fun createImageUri(context: Context): Uri {
        val dir = File(context.cacheDir, "camera_photos").apply { mkdirs() }
        val file = File(dir, "capture-${UUID.randomUUID()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}

@Composable
fun rememberCameraCaptureLauncher(
    onPhotoCaptured: (Uri) -> Unit
): () -> Unit {
    val context = LocalContext.current
    var pendingUri by remember { mutableStateOf<Uri?>(null) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingUri?.let(onPhotoCaptured)
        }
        pendingUri = null
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) {
            val uri = CameraPhotoHelper.createImageUri(context)
            pendingUri = uri
            takePictureLauncher.launch(uri)
        }
    }

    return {
        if (hasCameraPermission) {
            val uri = CameraPhotoHelper.createImageUri(context)
            pendingUri = uri
            takePictureLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

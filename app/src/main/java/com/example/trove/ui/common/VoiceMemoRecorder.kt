package com.example.trove.ui.common

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.trove.R
import com.example.trove.data.VoicePlayerHelper
import com.example.trove.data.VoiceRecorderHelper
import com.example.trove.ui.theme.AutumnBrown
import com.example.trove.ui.theme.AutumnOrange
import kotlinx.coroutines.delay

@Composable
fun VoiceMemoRecorder(
    voiceMemoUrl: String,
    voiceDuration: String,
    onRecordingComplete: (url: String, duration: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var recordingStartedAt by remember { mutableLongStateOf(0L) }
    var elapsedMs by remember { mutableLongStateOf(0L) }
    var isPlaying by remember { mutableStateOf(false) }

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (granted) {
            VoiceRecorderHelper.start(context)
            isRecording = true
            recordingStartedAt = System.currentTimeMillis()
            elapsedMs = 0L
        }
    }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            elapsedMs = System.currentTimeMillis() - recordingStartedAt
            delay(250)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (VoiceRecorderHelper.isRecording()) {
                VoiceRecorderHelper.stopRecording()
            }
            VoicePlayerHelper.stop()
        }
    }

    fun startRecording() {
        if (hasMicPermission) {
            VoiceRecorderHelper.start(context)
            isRecording = true
            recordingStartedAt = System.currentTimeMillis()
            elapsedMs = 0L
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun stopRecording() {
        val result = VoiceRecorderHelper.stopRecording()
        isRecording = false
        result?.let { recording ->
            val uri = VoiceRecorderHelper.fileUri(context, recording.file).toString()
            val duration = VoiceRecorderHelper.formatDuration(recording.durationMs)
            onRecordingComplete(uri, duration)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isRecording) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recording ${VoiceRecorderHelper.formatDuration(elapsedMs)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AutumnOrange,
                    fontWeight = FontWeight.Medium
                )
                Button(
                    onClick = ::stopRecording,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AutumnBrown,
                        contentColor = Color.White
                    )
                ) {
                    Text("Stop")
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = ::startRecording,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AutumnBrown,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mic),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (voiceMemoUrl.isBlank()) "Record" else "Re-record",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                if (voiceMemoUrl.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            if (VoicePlayerHelper.isPlaying(voiceMemoUrl)) {
                                VoicePlayerHelper.stop()
                                isPlaying = false
                            } else {
                                VoicePlayerHelper.play(
                                    context = context,
                                    url = voiceMemoUrl,
                                    onComplete = { isPlaying = false }
                                )
                                isPlaying = true
                            }
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(if (isPlaying) "Pause" else "Play")
                    }
                }
            }

            if (voiceDuration.isNotBlank()) {
                Text(
                    text = "Duration: $voiceDuration",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun VoiceMemoPlayButton(
    voiceMemoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember(voiceMemoUrl) { mutableStateOf(false) }

    DisposableEffect(voiceMemoUrl) {
        onDispose { VoicePlayerHelper.stop() }
    }

    Surface(
        modifier = modifier.size(36.dp),
        shape = CircleShape,
        color = AutumnOrange.copy(alpha = 0.15f),
        onClick = {
            if (isPlaying) {
                VoicePlayerHelper.stop()
                isPlaying = false
            } else {
                VoicePlayerHelper.play(
                    context = context,
                    url = voiceMemoUrl,
                    onComplete = { isPlaying = false }
                )
                isPlaying = true
            }
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = if (isPlaying) "⏸" else "▶",
                color = AutumnOrange
            )
        }
    }
}

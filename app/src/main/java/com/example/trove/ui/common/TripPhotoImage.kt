package com.example.trove.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import com.example.trove.data.LocalMediaStorage

@Composable
fun TripPhotoImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val context = LocalContext.current

    if (url.isBlank()) {
        ImagePlaceholder(modifier = modifier)
        return
    }

    val model = remember(url) {
        ImageRequest.Builder(context)
            .data(LocalMediaStorage.imageLoaderData(url))
            .build()
    }

    SubcomposeAsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        loading = { ImagePlaceholder(modifier = Modifier.fillMaxSize()) },
        error = { ImagePlaceholder(modifier = Modifier.fillMaxSize()) }
    )
}

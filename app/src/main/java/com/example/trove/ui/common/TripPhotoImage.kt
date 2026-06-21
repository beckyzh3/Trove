package com.example.trove.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.SubcomposeAsyncImage

@Composable
fun TripPhotoImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    if (url.isBlank()) {
        ImagePlaceholder(modifier = modifier)
        return
    }

    SubcomposeAsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        loading = { ImagePlaceholder(modifier = Modifier.fillMaxSize()) },
        error = { ImagePlaceholder(modifier = Modifier.fillMaxSize()) }
    )
}

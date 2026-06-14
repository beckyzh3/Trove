package com.example.trove.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun UserProfilePicture (
    modifier: Modifier = Modifier
){
    // placeholder pfp for now
    // eventually change to variable -> function -> upload
    val image = painterResource(id = com.example.trove.R.drawable.catpfp)

    Box(
        modifier = Modifier.size(120.dp)
    ) {
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )

    }
}

@Preview(showBackground = true)
@Composable
fun UserProfilePicturePreview() {
    UserProfilePicture()
}

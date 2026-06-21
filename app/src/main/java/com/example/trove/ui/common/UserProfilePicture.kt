package com.example.trove.ui.common

import androidx.compose.foundation.Image
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
import coil3.compose.AsyncImage
import com.example.trove.R

@Composable
fun UserProfilePicture (
    profilePicture: String,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
    ) {
        if (profilePicture.isBlank()) {
            Image(
                painter = painterResource(
                    id = R.drawable.catpfp
                ),
                contentDescription = "Default profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = profilePicture,
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                fallback = painterResource(
                    id = R.drawable.catpfp
                ),
                error = painterResource(
                    id = R.drawable.catpfp
                )
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun UserProfilePicturePreview() {
    UserProfilePicture(
        profilePicture = ""
    )
}

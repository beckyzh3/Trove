package com.example.trove.data

fun String.isLocalPhotoUri(): Boolean =
    startsWith("content://") || startsWith("file://")

fun String.isRemotePhotoUrl(): Boolean =
    startsWith("http://") || startsWith("https://")

package com.example.trove.data

fun String.isLocalMediaUri(): Boolean =
    startsWith("content://") || startsWith("file://")

fun String.isAssetMediaUri(): Boolean =
    startsWith("asset://")

/** @deprecated use [isLocalMediaUri] */
fun String.isLocalPhotoUri(): Boolean = isLocalMediaUri()

fun String.isRemoteMediaUrl(): Boolean =
    startsWith("http://") || startsWith("https://")

/** @deprecated use [isRemoteMediaUrl] */
fun String.isRemotePhotoUrl(): Boolean = isRemoteMediaUrl()

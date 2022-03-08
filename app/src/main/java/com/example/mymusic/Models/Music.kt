package com.example.mymusic.Models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Music(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val album: String,
    val albumArtUri: Uri,
    var fileUri: Uri
): Parcelable


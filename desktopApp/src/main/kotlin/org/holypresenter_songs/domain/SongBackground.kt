package org.holypresenter_songs.domain

import kotlinx.serialization.Serializable

@Serializable
sealed interface SongBackground {
    @Serializable
    data object None : SongBackground

    @Serializable
    data class Image(val path: String) : SongBackground

    @Serializable
    data class Video(val path: String) : SongBackground
}
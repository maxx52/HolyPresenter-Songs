package org.holypresenter_songs.domain

sealed interface SongBackground {
    data object None : SongBackground
    data class Image(val path: String) : SongBackground
    data class Video(val path: String) : SongBackground
}
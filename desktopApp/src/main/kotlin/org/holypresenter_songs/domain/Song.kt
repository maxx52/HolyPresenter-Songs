package org.holypresenter_songs.domain

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: SongId,
    val metadata: SongMetadata,
    val sections: List<SongSection>,
    val theme: SongTheme = SongTheme()
)
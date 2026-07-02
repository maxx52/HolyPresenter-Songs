package org.holypresenter_songs.domain

data class Song(
    val id: SongId,
    val metadata: SongMetadata,
    val sections: List<SongSection>,
    val theme: SongTheme = SongTheme()
)
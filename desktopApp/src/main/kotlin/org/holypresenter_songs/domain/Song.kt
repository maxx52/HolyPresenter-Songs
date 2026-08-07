package org.holypresenter_songs.domain

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: SongId,
    val metadata: SongMetadata,
    val sections: List<SongSection>,
    val executionOrder: List<SongOrderEntry> = sections.map { section ->
        SongOrderEntry(
            sectionId = section.id
        )
    },
    val theme: SongTheme = SongTheme()
)
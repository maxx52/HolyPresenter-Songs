package org.holypresenter_songs.domain.factory

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongId
import org.holypresenter_songs.domain.SongMetadata
import org.holypresenter_songs.domain.SongOrderEntry
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSectionType
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.SongTheme

object SongFactory {
    fun createEmpty(): Song {
        val initialSection =
            SongSection(
                type = SongSectionType.VERSE,
                number = 1,
                slides = listOf(
                    SongSlide(
                        lines = listOf("")
                    )
                )
            )

        return Song(
            id = SongId.random(),
            metadata = SongMetadata(
                title = "Новая песня",
                author = ""
            ),
            sections = listOf(initialSection),
            executionOrder = listOf(
                SongOrderEntry(
                    sectionId = initialSection.id
                )
            ),
            theme = SongTheme()
        )
    }
}
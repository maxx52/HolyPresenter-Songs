package org.holypresenter_songs.domain.factory

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongMetadata
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSectionType
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.SongTheme
import java.util.UUID
import org.holypresenter_songs.domain.SongId

object SongFactory {
    fun createEmpty(): Song =
        Song(
            id = SongId.random(),
            metadata = SongMetadata(
                title = "Новая песня",
                author = ""
            ),
            sections = listOf(
                SongSection(
                    type = SongSectionType.VERSE,
                    number = 1,
                    slides = listOf(
                        SongSlide(
                            lines = listOf("")
                        )
                    )
                )
            ),
            theme = SongTheme()
        )
}
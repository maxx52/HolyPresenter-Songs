package org.holypresenter_songs.service

import org.holypresenter_songs.domain.*

class InMemorySongService : SongService {
    override val songs: List<Song> = listOf(
        Song(
            id = SongId("amazing-grace"),
            metadata = SongMetadata(
                title = "Amazing Grace",
                author = "John Newton",
                language = "en"
            ),
            sections = listOf(
                SongSection(
                    type = SongSectionType.VERSE,
                    number = 1,
                    lines = listOf(
                        "Amazing grace! How sweet the sound",
                        "That saved a wretch like me"
                    )
                ),
                SongSection(
                    type = SongSectionType.CHORUS,
                    lines = listOf(
                        "Amazing grace, amazing grace",
                        "How sweet the sound"
                    )
                )
            )
        )
    )
}
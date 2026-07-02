package org.holypresenter_songs.repository

import org.holypresenter_songs.domain.*

class InMemorySongRepository : SongRepository {
    private val songs = mutableListOf(
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
                    slides = listOf(
                        SongSlide(
                            lines = listOf(
                                "Amazing grace! How sweet the sound",
                                "That saved a wretch like me"
                            )
                        ),
                        SongSlide(
                            lines = listOf(
                                "I once was lost, but now am found",
                                "Was blind, but now I see"
                            )
                        )
                    )
                )
            )
        )
    )

    override fun getAll(): List<Song> = songs.toList()

    override fun findById(id: SongId): Song? =
        songs.firstOrNull { it.id == id }

    override fun save(song: Song) {
        val index = songs.indexOfFirst { it.id == song.id }
        if (index >= 0) songs[index] = song else songs += song
    }

    override fun delete(id: SongId) {
        songs.removeAll { it.id == id }
    }
}
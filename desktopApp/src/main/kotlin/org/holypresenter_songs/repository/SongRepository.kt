package org.holypresenter_songs.repository

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongId

interface SongRepository {
    fun getAll(): List<Song>
    fun findById(id: SongId): Song?
    fun save(song: Song)
    fun delete(id: SongId)
}
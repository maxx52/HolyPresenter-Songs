package org.holypresenter_songs.service

import org.holypresenter_songs.domain.Song

interface SongService {
    val songs: List<Song>
}
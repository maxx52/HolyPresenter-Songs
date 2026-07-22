package org.holypresenter_songs.presentation.workspace

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.holypresenter_songs.domain.SongId

class SongWorkspaceState {
    var screen: SongScreen by mutableStateOf(SongScreen.LIBRARY)
        private set

    var selectedSongId: SongId? by mutableStateOf(null)
        private set

    fun openLibrary() {
        screen = SongScreen.LIBRARY
        selectedSongId = null
    }

    fun openEditor(songId: SongId) {
        selectedSongId = songId
        screen = SongScreen.EDITOR
    }
}
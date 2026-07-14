package org.holypresenter_songs.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide

class SongEditorState {
    var song: Song? by mutableStateOf(null)
    var selectedSection: SongSection? by mutableStateOf(null)
    var selectedSlide: SongSlide? by mutableStateOf(null)
    var previewOverlayOpacity: Float? by mutableStateOf(null)
    var previewFontSize: Int? by mutableStateOf(null)

    fun updateSong(song: Song?) {
        this.song = song

        if (song == null) {
            selectedSection = null
            selectedSlide = null
            return
        }

        if (selectedSection !in song.sections) {
            selectedSection = song.sections.firstOrNull()
        }

        if (selectedSlide !in selectedSection?.slides.orEmpty()) {
            selectedSlide = selectedSection
                ?.slides
                ?.firstOrNull()
        }
    }

    fun selectSection(section: SongSection) {
        selectedSection = section
        selectedSlide = section.slides.firstOrNull()
    }

    fun selectSlide(
        section: SongSection,
        slide: SongSlide
    ) {
        selectedSection = section
        selectedSlide = slide
    }
}
package org.holypresenter_songs.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import holypresenter.org.platform.api.model.HolyId
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongOrderEntry
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide

class SongEditorState {
    var song: Song? by mutableStateOf(null)
    var selectedSection: SongSection? by mutableStateOf(null)
    var selectedSlide: SongSlide? by mutableStateOf(null)

    /**
     * Выбранное вхождение секции
     * в порядке исполнения.
     */
    var selectedOrderEntryId: HolyId? by mutableStateOf(null)
        private set

    var previewOverlayOpacity: Float? by mutableStateOf(null)
    var previewFontSize: Int? by mutableStateOf(null)

    fun updateSong(
        song: Song?
    ) {
        this.song = song

        if (song == null) {
            clearSelection()
            return
        }

        /*
         * Сохраняем выбор в порядке исполнения,
         * пока соответствующее вхождение существует.
         */
        val currentOrderEntryId = selectedOrderEntryId

        if (
            currentOrderEntryId != null &&
            song.executionOrder.none {
                it.id == currentOrderEntryId
            }
        ) {
            selectedOrderEntryId = null
        }

        /*
         * Ищем обновлённый объект секции по id.
         * Это сохраняет выбор после выполнения команд,
         * которые создают copy секции или песни.
         */
        val selectedSectionId = selectedSection?.id

        selectedSection =
            selectedSectionId
                ?.let { sectionId ->
                    song.sections.firstOrNull {
                        it.id == sectionId
                    }
                }
                ?: song.sections.firstOrNull()

        val currentSection = selectedSection
        val selectedSlideId = selectedSlide?.id

        selectedSlide =
            selectedSlideId
                ?.let { slideId ->
                    currentSection
                        ?.slides
                        ?.firstOrNull {
                            it.id == slideId
                        }
                }
                ?: currentSection
                    ?.slides
                    ?.firstOrNull()
    }

    /**
     * Выбор секции в панели структуры.
     */
    fun selectSection(
        section: SongSection
    ) {
        selectedOrderEntryId = null
        selectedSection = section
        selectedSlide = section.slides.firstOrNull()
    }

    /**
     * Выбор отдельного слайда
     * в панели структуры.
     */
    fun selectSlide(
        section: SongSection,
        slide: SongSlide
    ) {
        selectedOrderEntryId = null
        selectedSection = section
        selectedSlide = slide
    }

    /**
     * Выбор элемента порядка исполнения.
     */
    fun selectOrderEntry(
        entry: SongOrderEntry,
        section: SongSection
    ) {
        if (entry.sectionId != section.id) {
            return
        }

        selectedOrderEntryId = entry.id
        selectedSection = section
        selectedSlide = section.slides.firstOrNull()
    }

    fun clearSelection() {
        selectedOrderEntryId = null
        selectedSection = null
        selectedSlide = null
    }
}
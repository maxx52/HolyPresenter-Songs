package org.holypresenter_songs.domain.editor

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide

class SongEditor {
    fun addSection(song: Song, section: SongSection): Song =
        song.copy(sections = song.sections + section)

    fun addSlide(song: Song, section: SongSection): Song =
        updateSection(song, section) {
            it.copy(
                slides = it.slides + SongSlide(lines = listOf(""))
            )
        }

    fun updateSlideText(
        song: Song,
        section: SongSection,
        slide: SongSlide,
        text: String
    ): Song =
        updateSection(song, section) { currentSection ->
            currentSection.copy(
                slides = currentSection.slides.map { currentSlide ->
                    if (currentSlide == slide) {
                        currentSlide.copy(lines = text.lines())
                    } else {
                        currentSlide
                    }
                }
            )
        }

    fun deleteSlide(
        song: Song,
        section: SongSection,
        slide: SongSlide
    ): Song {
        val updatedSections = song.sections.mapNotNull { currentSection ->
            if (currentSection != section) {
                currentSection
            } else {
                val updatedSlides = currentSection.slides - slide

                if (updatedSlides.isEmpty()) {
                    null
                } else {
                    currentSection.copy(slides = updatedSlides)
                }
            }
        }

        return song.copy(sections = updatedSections)
    }

    fun duplicateSlide(
        song: Song,
        section: SongSection,
        slide: SongSlide
    ): Song =
        updateSection(song, section) { currentSection ->
            val index = currentSection.slides.indexOf(slide)
            val slides = currentSection.slides.toMutableList()

            if (index >= 0) {
                slides.add(index + 1, slide.copy())
            }

            currentSection.copy(slides = slides)
        }

    fun deleteSection(song: Song, section: SongSection): Song =
        song.copy(sections = song.sections - section)

    fun duplicateSection(song: Song, section: SongSection): Song {
        val index = song.sections.indexOf(section)
        val sections = song.sections.toMutableList()

        if (index >= 0) {
            sections.add(index + 1, section.copy())
        }

        return song.copy(sections = sections)
    }

    private fun updateSection(
        song: Song,
        section: SongSection,
        transform: (SongSection) -> SongSection
    ): Song =
        song.copy(
            sections = song.sections.map { currentSection ->
                if (currentSection == section) {
                    transform(currentSection)
                } else {
                    currentSection
                }
            }
        )
}
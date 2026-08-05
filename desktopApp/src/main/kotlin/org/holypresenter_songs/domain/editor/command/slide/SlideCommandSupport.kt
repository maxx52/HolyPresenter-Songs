package org.holypresenter_songs.domain.editor.command.slide

import holypresenter.org.platform.api.model.HolyId
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSlide

internal fun Song.replaceSectionSlides(
    sectionId: HolyId,
    expectedSlides: List<SongSlide>,
    replacementSlides: List<SongSlide>
): Song {
    val sectionIndex =
        sections.indexOfFirst { section ->
            section.id == sectionId
        }

    if (sectionIndex == -1) {
        return this
    }

    val currentSection = sections[sectionIndex]

    if (currentSection.slides != expectedSlides) {
        return this
    }

    val updatedSections = sections.toMutableList()

    updatedSections[sectionIndex] =
        currentSection.copy(
            slides = replacementSlides
        )

    return copy(sections = updatedSections)
}

internal fun SongSlide.alignedChords(): List<String?> =
    List(lines.size) { index ->
        chords.getOrNull(index)
    }

internal fun List<String?>.compactChordLines(): List<String?> =
    if (all { chord -> chord.isNullOrBlank() }) {
        emptyList()
    } else {
        this
    }
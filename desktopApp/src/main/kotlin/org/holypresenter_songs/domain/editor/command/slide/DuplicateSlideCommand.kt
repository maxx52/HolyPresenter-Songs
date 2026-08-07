package org.holypresenter_songs.domain.editor.command.slide

import holypresenter.org.platform.api.model.HolyIds
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class DuplicateSlideCommand(
    private val sourceSection: SongSection,
    private val sourceSlide: SongSlide
) : SongEditCommand {
    override val description: String = "Дублировать слайд"

    /**
     * Создаётся только один раз.
     * При Redo используется тот же объект и тот же ID.
     */
    private val duplicateSlide =
        SongSlide(
            id = HolyIds.newId(),
            lines = sourceSlide.lines.toList(),
            chords = sourceSlide.chords.toList()
        )

    private var insertIndex: Int? = null

    override fun execute(
        song: Song
    ): Song {
        val sectionIndex =
            song.sections.indexOfFirst { section ->
                section.id == sourceSection.id
            }

        if (sectionIndex == -1) {
            return song
        }

        val currentSection = song.sections[sectionIndex]

        if (
            currentSection.slides.any { slide ->
                slide.id == duplicateSlide.id
            }
        ) {
            return song
        }

        val sourceSlideIndex =
            currentSection.slides.indexOfFirst { slide ->
                slide.id == sourceSlide.id
            }

        if (sourceSlideIndex == -1) {
            return song
        }

        val slides = currentSection.slides.toMutableList()

        val targetIndex =
            insertIndex
                ?: (sourceSlideIndex + 1).also {
                    insertIndex = it
                }

        slides.add(
            index = targetIndex.coerceIn(
                minimumValue = 0,
                maximumValue = slides.size
            ),
            element = duplicateSlide
        )

        val sections = song.sections.toMutableList()

        sections[sectionIndex] =
            currentSection.copy(
                slides = slides
            )

        return song.copy(
            sections = sections
        )
    }

    override fun undo(
        song: Song
    ): Song {
        val sectionIndex =
            song.sections.indexOfFirst { section ->
                section.id == sourceSection.id
            }

        if (sectionIndex == -1) {
            return song
        }

        val currentSection = song.sections[sectionIndex]

        val duplicateIndex =
            currentSection.slides.indexOfFirst { slide ->
                slide.id == duplicateSlide.id
            }

        if (duplicateIndex == -1) {
            return song
        }

        val slides = currentSection.slides.toMutableList()

        slides.removeAt(duplicateIndex)

        val sections = song.sections.toMutableList()

        sections[sectionIndex] =
            currentSection.copy(
                slides = slides
            )

        return song.copy(
            sections = sections
        )
    }
}
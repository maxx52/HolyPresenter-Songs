package org.holypresenter_songs.domain.editor.command.section

import holypresenter.org.platform.api.model.HolyIds
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class DuplicateSectionCommand(
    private val sourceSection: SongSection
) : SongEditCommand {
    override val description: String = "Дублировать секцию"

    /**
     * Дубликат создаётся один раз вместе с командой.
     *
     * Это важно для Undo/Redo: при повторном выполнении
     * команды идентификаторы не должны изменяться.
     */
    private val duplicateSection =
        SongSection(
            id = HolyIds.newId(),
            type = sourceSection.type,
            number = sourceSection.number,
            slides =
                sourceSection.slides.map { sourceSlide ->
                    SongSlide(
                        id = HolyIds.newId(),
                        lines = sourceSlide.lines.toList(),
                        chords = sourceSlide.chords.toList()
                    )
                }
        )

    private var duplicateIndex: Int? = null

    override fun execute(
        song: Song
    ): Song {
        /*
         * Защита от повторного добавления
         * одного и того же дубликата.
         */
        if (
            song.sections.any { section ->
                section.id == duplicateSection.id
            }
        ) {
            return song
        }

        val sourceIndex =
            song.sections.indexOfFirst { section ->
                section.id == sourceSection.id
            }

        if (sourceIndex == -1) {
            return song
        }

        val updatedSections = song.sections.toMutableList()

        val insertIndex =
            duplicateIndex
                ?: (sourceIndex + 1).also { index ->
                    duplicateIndex = index
                }

        updatedSections.add(
            index = insertIndex.coerceIn(
                minimumValue = 0,
                maximumValue = updatedSections.size
            ),
            element = duplicateSection
        )

        return song.copy(
            sections = updatedSections
        )
    }

    override fun undo(
        song: Song
    ): Song {
        val currentDuplicateIndex =
            song.sections.indexOfFirst { section ->
                section.id == duplicateSection.id
            }

        if (currentDuplicateIndex == -1) {
            return song
        }

        val updatedSections = song.sections.toMutableList()

        updatedSections.removeAt(
            currentDuplicateIndex
        )

        return song.copy(
            sections = updatedSections
        )
    }
}
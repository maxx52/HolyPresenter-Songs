package org.holypresenter_songs.domain.editor.command.section

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class DeleteSectionCommand(
    private val section: SongSection
) : SongEditCommand {
    override val description: String = "Удалить секцию"

    /*
     * Запоминается при первом выполнении команды.
     * При redo индекс повторно не перезаписывается.
     */
    private var originalIndex: Int? = null

    override fun execute(
        song: Song
    ): Song {
        val currentIndex =
            song.sections.indexOfFirst {
                it.id == section.id
            }

        if (currentIndex == -1) {
            return song
        }

        if (originalIndex == null) {
            originalIndex = currentIndex
        }

        val updatedSections = song.sections.toMutableList()

        updatedSections.removeAt(currentIndex)

        return song.copy(
            sections = updatedSections
        )
    }

    override fun undo(
        song: Song
    ): Song {
        /*
         * Не добавляем секцию повторно,
         * если она уже присутствует.
         */
        if (
            song.sections.any {
                it.id == section.id
            }
        ) {
            return song
        }

        val updatedSections = song.sections.toMutableList()

        val insertIndex =
            originalIndex
                ?.coerceIn(
                    minimumValue = 0,
                    maximumValue = updatedSections.size
                )
                ?: updatedSections.size

        updatedSections.add(
            index = insertIndex,
            element = section
        )

        return song.copy(
            sections = updatedSections
        )
    }
}
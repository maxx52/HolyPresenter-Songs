package org.holypresenter_songs.domain.editor.command.section

import holypresenter.org.platform.api.model.HolyId
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSectionType
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class ChangeSectionTypeCommand(
    private val sectionId: HolyId,
    private val newType: SongSectionType
) : SongEditCommand {
    override val description: String = "Сменить тип секции"

    /*
     * Эти значения запоминаются при первом execute.
     * Поэтому Undo/Redo всегда возвращают
     * точно тот же тип и номер.
     */
    private var originalType: SongSectionType? = null
    private var originalNumber: Int? = null
    private var changedNumber: Int? = null

    override fun execute(
        song: Song
    ): Song {
        val sectionIndex =
            song.sections.indexOfFirst {
                it.id == sectionId
            }

        if (sectionIndex == -1) {
            return song
        }

        val section = song.sections[sectionIndex]

        /*
         * Первое выполнение команды.
         */
        if (originalType == null) {
            if (section.type == newType) {
                return song
            }

            originalType = section.type
            originalNumber = section.number

            /*
             * Новый номер вычисляется только один раз.
             *
             * Например:
             *
             * Бридж 1
             * Тег 1 -> Бридж
             *
             * Получаем Бридж 2.
             */
            changedNumber =
                song.sections
                    .asSequence()
                    .filter {
                        it.id != section.id && it.type == newType
                    }
                    .maxOfOrNull {
                        it.number
                    }
                    ?.plus(1)
                    ?: 1
        }

        val targetNumber = changedNumber ?: return song

        if (
            section.type == newType &&
            section.number == targetNumber
        ) {
            return song
        }

        val updatedSections = song.sections.toMutableList()

        updatedSections[sectionIndex] =
            section.copy(
                type = newType,
                number = targetNumber
            )

        return song.copy(
            sections = updatedSections
        )
    }

    override fun undo(
        song: Song
    ): Song {
        val oldType = originalType ?: return song
        val oldNumber = originalNumber ?: return song

        val sectionIndex =
            song.sections.indexOfFirst {
                it.id == sectionId
            }

        if (sectionIndex == -1) {
            return song
        }

        val section = song.sections[sectionIndex]
        val updatedSections = song.sections.toMutableList()

        updatedSections[sectionIndex] =
            section.copy(
                type = oldType,
                number = oldNumber
            )

        return song.copy(
            sections = updatedSections
        )
    }
}
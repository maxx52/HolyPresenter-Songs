package org.holypresenter_songs.domain.editor.command.order

import holypresenter.org.platform.api.model.HolyId
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongOrderEntry
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class AddSongOrderEntryCommand(
    sectionId: HolyId
) : SongEditCommand {
    override val description: String = "Добавить секцию в порядок исполнения"

    /*
     * Entry создаётся один раз.
     *
     * Поэтому после Undo -> Redo
     * возвращается то же самое вхождение
     * с тем же уникальным id.
     */
    val entry = SongOrderEntry(sectionId = sectionId)

    override fun execute(
        song: Song
    ): Song {
        /*
         * Нельзя добавить в порядок
         * несуществующую секцию.
         */
        if (
            song.sections.none {
                it.id == entry.sectionId
            }
        ) {
            return song
        }

        /*
         * Защита от повторного добавления
         * именно этого entry.
         *
         * Одна и та же секция при этом
         * может иметь сколько угодно
         * разных SongOrderEntry.
         */
        if (
            song.executionOrder.any {
                it.id == entry.id
            }
        ) {
            return song
        }

        return song.copy(
            executionOrder = song.executionOrder + entry
        )
    }

    override fun undo(
        song: Song
    ): Song {
        if (
            song.executionOrder.none {
                it.id == entry.id
            }
        ) {
            return song
        }

        return song.copy(
            executionOrder =
                song.executionOrder.filterNot {
                    it.id == entry.id
                }
        )
    }
}
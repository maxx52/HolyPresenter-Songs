package org.holypresenter_songs.domain.editor.command.order

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongOrderEntry
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class DeleteSongOrderEntryCommand(
    private val entry: SongOrderEntry
) : SongEditCommand {

    override val description: String = "Удалить элемент из порядка исполнения"

    /*
     * Запоминаем позицию при первом выполнении.
     * Она понадобится для Undo.
     */
    private var originalIndex: Int? = null

    override fun execute(
        song: Song
    ): Song {
        val entryIndex =
            song.executionOrder.indexOfFirst {
                it.id == entry.id
            }

        if (entryIndex == -1) {
            return song
        }

        if (originalIndex == null) {
            originalIndex = entryIndex
        }

        val updatedOrder =
            song.executionOrder.toMutableList()

        updatedOrder.removeAt(entryIndex)

        return song.copy(
            executionOrder = updatedOrder
        )
    }

    override fun undo(
        song: Song
    ): Song {
        /*
         * Не добавляем вхождение повторно.
         */
        if (
            song.executionOrder.any {
                it.id == entry.id
            }
        ) {
            return song
        }

        val updatedOrder = song.executionOrder.toMutableList()

        val insertIndex =
            originalIndex
                ?.coerceIn(
                    minimumValue = 0,
                    maximumValue = updatedOrder.size
                )
                ?: updatedOrder.size

        updatedOrder.add(
            index = insertIndex,
            element = entry
        )

        return song.copy(
            executionOrder = updatedOrder
        )
    }
}
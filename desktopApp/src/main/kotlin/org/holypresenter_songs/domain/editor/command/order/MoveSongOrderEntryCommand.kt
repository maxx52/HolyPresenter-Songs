package org.holypresenter_songs.domain.editor.command.order

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class MoveSongOrderEntryCommand(
    private val fromIndex: Int,
    private val toIndex: Int
) : SongEditCommand {
    override val description: String = "Переместить элемент порядка исполнения"

    override fun execute(
        song: Song
    ): Song =
        moveEntry(
            song = song,
            fromIndex = fromIndex,
            toIndex = toIndex
        )

    override fun undo(
        song: Song
    ): Song =
        moveEntry(
            song = song,
            fromIndex = toIndex,
            toIndex = fromIndex
        )

    private fun moveEntry(
        song: Song,
        fromIndex: Int,
        toIndex: Int
    ): Song {
        if (fromIndex == toIndex) {
            return song
        }

        if (fromIndex !in song.executionOrder.indices) {
            return song
        }

        if (toIndex !in song.executionOrder.indices) {
            return song
        }

        val updatedOrder = song.executionOrder.toMutableList()
        val movedEntry = updatedOrder.removeAt(fromIndex)

        updatedOrder.add(
            index = toIndex,
            element = movedEntry
        )

        return song.copy(
            executionOrder = updatedOrder
        )
    }
}
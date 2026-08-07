package org.holypresenter_songs.domain.editor.command

import org.holypresenter_songs.domain.Song

class SongEditCommandStack {
    private val undoStack = ArrayDeque<SongEditCommand>()
    private val redoStack = ArrayDeque<SongEditCommand>()

    val canUndo: Boolean
        get() = undoStack.isNotEmpty()

    val canRedo: Boolean
        get() = redoStack.isNotEmpty()

    val undoDescription: String?
        get() = undoStack.lastOrNull()?.description

    val redoDescription: String?
        get() = redoStack.lastOrNull()?.description

    fun execute(
        song: Song,
        command: SongEditCommand
    ): Song {
        val updatedSong = command.execute(song)

        /*
         * Пустые команды в историю не добавляются.
         */
        if (updatedSong == song) {
            return song
        }
        undoStack.addLast(command)
        redoStack.clear()
        return updatedSong
    }

    fun undo(
        song: Song
    ): Song? {
        val command = undoStack.removeLastOrNull() ?: return null
        val updatedSong = command.undo(song)

        /*
         * Одна операция Undo всегда соответствует
         * ровно одной команде.
         */
        redoStack.addLast(command)
        return updatedSong
    }

    fun redo(
        song: Song
    ): Song? {
        val command = redoStack.removeLastOrNull() ?: return null
        val updatedSong = command.execute(song)

        undoStack.addLast(command)
        return updatedSong
    }

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }
}
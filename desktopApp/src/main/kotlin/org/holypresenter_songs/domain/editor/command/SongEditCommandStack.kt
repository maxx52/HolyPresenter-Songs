package org.holypresenter_songs.domain.editor.command

import org.holypresenter_songs.domain.Song

class SongEditCommandStack {
    private val undoStack = ArrayDeque<SongEditCommand>()
    private val redoStack = ArrayDeque<SongEditCommand>()

    fun execute(song: Song, command: SongEditCommand): Song {
        val updated = command.execute(song)
        undoStack.addLast(command)
        redoStack.clear()
        return updated
    }

    fun undo(song: Song): Song? {
        val command = undoStack.removeLastOrNull() ?: return null
        redoStack.addLast(command)
        return command.undo(song)
    }

    fun redo(song: Song): Song? {
        val command = redoStack.removeLastOrNull() ?: return null
        undoStack.addLast(command)
        return command.execute(song)
    }

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }
}
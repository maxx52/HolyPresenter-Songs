package org.holypresenter_songs.domain.editor

import org.holypresenter_songs.domain.editor.command.SongEditCommand
import org.holypresenter_songs.domain.editor.command.SongEditCommandStack
import org.holypresenter_songs.presentation.SongEditorState
import org.holypresenter_songs.repository.SongRepository

class SongEditor(
    private val repository: SongRepository,
    private val state: SongEditorState
) {
    private val commandStack = SongEditCommandStack()

    val canUndo: Boolean
        get() = commandStack.canUndo

    val canRedo: Boolean
        get() = commandStack.canRedo

    val undoDescription: String?
        get() = commandStack.undoDescription

    val redoDescription: String?
        get() = commandStack.redoDescription

    fun execute(command: SongEditCommand) {
        val currentSong = state.song ?: return
        val updatedSong = commandStack.execute(currentSong, command)

        repository.save(updatedSong)
        state.updateSong(updatedSong)
    }

    fun undo() {
        val currentSong = state.song ?: return

        commandStack.undo(currentSong)?.let {
            repository.save(it)
            state.updateSong(it)
        }
    }

    fun redo() {
        val currentSong = state.song ?: return

        commandStack.redo(currentSong)?.let {
            repository.save(it)
            state.updateSong(it)
        }
    }
}
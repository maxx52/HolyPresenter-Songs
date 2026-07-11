package org.holypresenter_songs.domain.editor.command

import org.holypresenter_songs.domain.Song

interface SongEditCommand {
    val description: String

    fun execute(song: Song): Song

    fun undo(song: Song): Song
}
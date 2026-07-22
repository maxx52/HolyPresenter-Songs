package org.holypresenter_songs.domain.editor.command.metadata

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class UpdateSongMetadataCommand(
    private val oldTitle: String,
    private val oldAuthor: String?,
    private val newTitle: String,
    private val newAuthor: String
) : SongEditCommand {
    override val description: String =
        "Изменить сведения о песне"

    override fun execute(song: Song): Song =
        update(
            song = song,
            title = newTitle,
            author = newAuthor
        )

    override fun undo(song: Song): Song =
        update(
            song = song,
            title = oldTitle,
            author = oldAuthor
        )

    private fun update(
        song: Song,
        title: String,
        author: String?
    ): Song =
        song.copy(
            metadata = song.metadata.copy(
                title = title,
                author = author
            )
        )
}
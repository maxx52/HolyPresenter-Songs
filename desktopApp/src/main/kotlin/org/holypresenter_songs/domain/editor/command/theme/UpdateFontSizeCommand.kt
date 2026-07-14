package org.holypresenter_songs.domain.editor.command.theme

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class UpdateFontSizeCommand(
    private val oldSize: Int,
    private val newSize: Int
) : SongEditCommand {
    override val description = "Изменить размер текста"

    override fun execute(song: Song): Song =
        update(song, newSize)

    override fun undo(song: Song): Song =
        update(song, oldSize)

    private fun update(
        song: Song,
        size: Int
    ): Song =
        song.copy(
            theme = song.theme.copy(
                textStyle = song.theme.textStyle.copy(
                    fontSize = size
                )
            )
        )
}
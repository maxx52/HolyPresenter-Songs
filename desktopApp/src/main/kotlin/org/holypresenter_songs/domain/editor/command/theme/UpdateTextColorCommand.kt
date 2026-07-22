package org.holypresenter_songs.domain.editor.command.theme

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class UpdateTextColorCommand(
    private val oldColor: Long,
    private val newColor: Long
) : SongEditCommand {
    override val description: String = "Изменить цвет текста"

    override fun execute(song: Song): Song =
        updateColor(song, newColor)

    override fun undo(song: Song): Song =
        updateColor(song, oldColor)

    private fun updateColor(
        song: Song,
        color: Long
    ): Song =
        song.copy(
            theme = song.theme.copy(
                textStyle = song.theme.textStyle.copy(
                    textColor = color
                )
            )
        )
}
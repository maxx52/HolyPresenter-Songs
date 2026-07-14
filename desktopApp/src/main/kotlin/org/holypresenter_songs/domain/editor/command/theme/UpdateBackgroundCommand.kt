package org.holypresenter_songs.domain.editor.command.theme

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongBackground
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class UpdateBackgroundCommand(
    private val oldBackground: SongBackground,
    private val newBackground: SongBackground
) : SongEditCommand {
    override val description = "Изменить фон"

    override fun execute(song: Song): Song =
        song.copy(
            theme = song.theme.copy(
                background = newBackground
            )
        )

    override fun undo(song: Song): Song =
        song.copy(
            theme = song.theme.copy(
                background = oldBackground
            )
        )
}
package org.holypresenter_songs.domain.editor.command.theme

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class UpdateOutlineEnabledCommand(
    private val oldValue: Boolean,
    private val newValue: Boolean
) : SongEditCommand {
    override val description: String = "Изменить контур текста"
    override fun execute(song: Song): Song = update(song, newValue)
    override fun undo(song: Song): Song = update(song, oldValue)

    private fun update(
        song: Song,
        enabled: Boolean
    ): Song =
        song.copy(
            theme = song.theme.copy(
                textStyle = song.theme.textStyle.copy(
                    outlineEnabled = enabled
                )
            )
        )
}
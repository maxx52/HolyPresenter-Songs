package org.holypresenter_songs.domain.editor.command.theme

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class UpdateFontFamilyCommand(
    private val oldFontFamily: String?,
    private val newFontFamily: String?
) : SongEditCommand {

    override val description: String = "Изменить шрифт"

    override fun execute(song: Song): Song =
        update(song, newFontFamily)

    override fun undo(song: Song): Song =
        update(song, oldFontFamily)

    private fun update(
        song: Song,
        fontFamily: String?
    ): Song =
        song.copy(
            theme = song.theme.copy(
                textStyle = song.theme.textStyle.copy(
                    fontFamily = fontFamily ?: "Arial"
                )
            )
        )
}
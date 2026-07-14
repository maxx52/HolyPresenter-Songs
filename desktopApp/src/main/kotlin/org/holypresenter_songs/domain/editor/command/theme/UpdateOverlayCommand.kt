package org.holypresenter_songs.domain.editor.command.theme

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class UpdateOverlayCommand(
    private val oldOpacity: Float,
    private val newOpacity: Float
) : SongEditCommand {
    override val description: String = "Изменить затемнение фона"

    override fun execute(song: Song): Song =
        updateOpacity(song, newOpacity)

    override fun undo(song: Song): Song =
        updateOpacity(song, oldOpacity)

    private fun updateOpacity(
        song: Song,
        opacity: Float
    ): Song =
        song.copy(
            theme = song.theme.copy(
                overlay = song.theme.overlay.copy(
                    opacity = opacity.coerceIn(0f, 1f)
                )
            )
        )
}
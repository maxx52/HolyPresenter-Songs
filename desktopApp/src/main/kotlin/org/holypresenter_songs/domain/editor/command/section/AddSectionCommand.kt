package org.holypresenter_songs.domain.editor.command.section

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class AddSectionCommand(
    private val section: SongSection
) : SongEditCommand {
    override val description: String = "Добавить секцию"

    override fun execute(song: Song): Song =
        song.copy(
            sections = song.sections + section
        )

    override fun undo(song: Song): Song =
        song.copy(
            sections = song.sections.dropLast(1)
        )
}
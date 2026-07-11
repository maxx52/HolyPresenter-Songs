package org.holypresenter_songs.domain.editor.command.section

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class DeleteSectionCommand(
    private val section: SongSection
) : SongEditCommand {
    private val index = -1
    override val description = "Удалить секцию"

    override fun execute(song: Song): Song =
        song.copy(
            sections = song.sections - section
        )

    override fun undo(song: Song): Song {
        val sections = song.sections.toMutableList()

        val insertIndex =
            if (index == -1)
                sections.size
            else
                index.coerceIn(0, sections.size)

        sections.add(insertIndex, section)

        return song.copy(
            sections = sections
        )
    }
}
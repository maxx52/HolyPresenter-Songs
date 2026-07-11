package org.holypresenter_songs.domain.editor.command.section

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class MoveSectionCommand(
    private val fromIndex: Int,
    private val toIndex: Int
) : SongEditCommand {
    override val description: String = "Переместить секцию"

    override fun execute(song: Song): Song =
        moveSection(
            song = song,
            from = fromIndex,
            to = toIndex
        )

    override fun undo(song: Song): Song =
        moveSection(
            song = song,
            from = toIndex,
            to = fromIndex
        )

    private fun moveSection(
        song: Song,
        from: Int,
        to: Int
    ): Song {
        if (from == to) {
            return song
        }

        if (from !in song.sections.indices) {
            return song
        }

        if (to !in song.sections.indices) {
            return song
        }
        val sections = song.sections.toMutableList()
        val movedSection = sections.removeAt(from)
        sections.add(to, movedSection)
        return song.copy(sections = sections)
    }
}
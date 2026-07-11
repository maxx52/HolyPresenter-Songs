package org.holypresenter_songs.domain.editor.command.section

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class DuplicateSectionCommand(
    private val section: SongSection
) : SongEditCommand {
    private var duplicateIndex: Int = -1
    override val description: String = "Дублировать секцию"

    override fun execute(song: Song): Song {
        val sourceIndex = song.sections.indexOf(section)

        if (sourceIndex < 0) {
            return song
        }
        duplicateIndex = sourceIndex + 1

        val sections = song.sections.toMutableList()
        sections.add(
            duplicateIndex,
            section.copy(
                slides = section.slides.map { slide ->
                    slide.copy(
                        lines = slide.lines.toList()
                    )
                }
            )
        )
        return song.copy(sections = sections)
    }

    override fun undo(song: Song): Song {
        if (duplicateIndex !in song.sections.indices) {
            return song
        }
        val sections = song.sections.toMutableList()
        sections.removeAt(duplicateIndex)
        return song.copy(sections = sections)
    }
}
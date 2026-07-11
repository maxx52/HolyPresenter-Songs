package org.holypresenter_songs.domain.editor

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.editor.command.SongEditCommandStack
import org.holypresenter_songs.domain.editor.command.UpdateSlideTextCommand
import org.holypresenter_songs.domain.editor.command.section.AddSectionCommand
import org.holypresenter_songs.domain.editor.command.section.DuplicateSectionCommand
import org.holypresenter_songs.domain.editor.command.slide.AddSlideCommand
import org.holypresenter_songs.domain.editor.command.slide.DeleteSlideCommand
import org.holypresenter_songs.domain.editor.command.section.MoveSectionCommand
import org.holypresenter_songs.domain.editor.command.slide.DuplicateSlideCommand

class SongEditor {
    private val commandStack = SongEditCommandStack()

    fun addSection(
        song: Song,
        section: SongSection
    ): Song =
        commandStack.execute(
            song,
            AddSectionCommand(section)
        )

    fun addSlide(
        song: Song,
        section: SongSection
    ): Song =
        commandStack.execute(
            song,
            AddSlideCommand(section)
        )

    fun updateSlideText(
        song: Song,
        section: SongSection,
        slide: SongSlide,
        text: String
    ): Song {
        val oldText = slide.lines.joinToString("\n")

        return commandStack.execute(
            song,
            UpdateSlideTextCommand(
                section = section,
                slide = slide,
                oldText = oldText,
                newText = text
            )
        )
    }

    fun deleteSlide(
        song: Song,
        section: SongSection,
        slide: SongSlide
    ): Song =
        commandStack.execute(
            song,
            DeleteSlideCommand(section, slide)
        )

    fun duplicateSlide(
        song: Song,
        section: SongSection,
        slide: SongSlide
    ): Song =
        commandStack.execute(
            song,
            DuplicateSlideCommand(section, slide)
        )

    fun deleteSection(song: Song, section: SongSection): Song =
        song.copy(sections = song.sections - section)

    fun duplicateSection(
        song: Song,
        section: SongSection
    ): Song =
        commandStack.execute(
            song,
            DuplicateSectionCommand(section)
        )

    fun moveSection(
        song: Song,
        fromIndex: Int,
        toIndex: Int
    ): Song =
        commandStack.execute(
            song,
            MoveSectionCommand(
                fromIndex = fromIndex,
                toIndex = toIndex
            )
        )

    private fun updateSection(
        song: Song,
        section: SongSection,
        transform: (SongSection) -> SongSection
    ): Song =
        song.copy(
            sections = song.sections.map { currentSection ->
                if (currentSection == section) {
                    transform(currentSection)
                } else {
                    currentSection
                }
            }
        )

    fun undo(song: Song): Song? =
        commandStack.undo(song)

    fun redo(song: Song): Song? =
        commandStack.redo(song)
}
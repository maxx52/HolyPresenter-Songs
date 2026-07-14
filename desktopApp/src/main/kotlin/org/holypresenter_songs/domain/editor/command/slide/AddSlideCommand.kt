package org.holypresenter_songs.domain.editor.command.slide

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class AddSlideCommand(
    private val section: SongSection
) : SongEditCommand {
    private var sectionIndex: Int = -1

    private val addedSlide = SongSlide(
        lines = listOf("")
    )

    override val description: String = "Добавить слайд"

    override fun execute(song: Song): Song {
        /*
         * При первом выполнении находим индекс исходной секции.
         * При redo используем уже сохранённый индекс.
         */
        if (sectionIndex == -1) {
            sectionIndex = song.sections.indexOf(section)
        }

        if (sectionIndex !in song.sections.indices) {
            return song
        }

        val sections = song.sections.toMutableList()
        val currentSection = sections[sectionIndex]

        sections[sectionIndex] = currentSection.copy(
            slides = currentSection.slides + addedSlide
        )
        return song.copy(sections = sections)
    }

    override fun undo(song: Song): Song {
        if (sectionIndex !in song.sections.indices) {
            return song
        }

        val sections = song.sections.toMutableList()
        val currentSection = sections[sectionIndex]
        val slides = currentSection.slides.toMutableList()

        /*
         * Удаляем именно слайд, созданный этой командой.
         * Обычно он будет последним, но поиск безопаснее.
         */
        val addedSlideIndex = slides.indexOfLast {
            it === addedSlide || it == addedSlide
        }

        if (addedSlideIndex == -1) {
            return song
        }

        slides.removeAt(addedSlideIndex)

        sections[sectionIndex] = currentSection.copy(
            slides = slides
        )
        return song.copy(sections = sections)
    }
}
package org.holypresenter_songs.domain.editor.command.slide

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class AddSlideCommand(
    private val section: SongSection
) : SongEditCommand {
    override val description = "Добавить слайд"

    override fun execute(song: Song): Song =
        song.copy(
            sections = song.sections.map { currentSection ->
                if (currentSection != section) {
                    currentSection
                } else {
                    currentSection.copy(
                        slides = currentSection.slides + SongSlide(
                            lines = listOf("")
                        )
                    )
                }
            }
        )

    override fun undo(song: Song): Song =
        song.copy(
            sections = song.sections.map { currentSection ->
                if (currentSection != section) {
                    currentSection
                } else {
                    currentSection.copy(
                        slides = currentSection.slides.dropLast(1)
                    )
                }
            }
        )
}
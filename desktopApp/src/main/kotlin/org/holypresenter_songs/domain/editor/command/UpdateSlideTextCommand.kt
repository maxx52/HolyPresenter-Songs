package org.holypresenter_songs.domain.editor.command

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide

class UpdateSlideTextCommand(
    private val section: SongSection,
    private val slide: SongSlide,
    private val oldText: String,
    private val newText: String
) : SongEditCommand {
    override val description: String = "Изменить текст слайда"
    override fun execute(song: Song): Song =
        updateSlide(song, newText)

    override fun undo(song: Song): Song =
        updateSlide(song, oldText)

    private fun updateSlide(
        song: Song,
        text: String
    ): Song {
        return song.copy(
            sections = song.sections.map { currentSection ->
                if (currentSection != section) {
                    currentSection
                } else {
                    currentSection.copy(
                        slides = currentSection.slides.map { currentSlide ->
                            if (currentSlide == slide) {
                                currentSlide.copy(
                                    lines = text.lines()
                                )
                            } else {
                                currentSlide
                            }
                        }
                    )
                }
            }
        )
    }
}
package org.holypresenter_songs.domain.editor.command.slide

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class DuplicateSlideCommand(
    private val section: SongSection,
    private val slide: SongSlide
) : SongEditCommand {
    private val originalIndex = section.slides.indexOf(slide)
    override val description: String = "Дублировать слайд"

    override fun execute(song: Song): Song =
        song.copy(
            sections = song.sections.map { currentSection ->
                if (currentSection != section) {
                    currentSection
                } else {
                    val slides = currentSection.slides.toMutableList()

                    if (originalIndex >= 0) {
                        slides.add(
                            originalIndex + 1,
                            slide.copy()
                        )
                    }
                    currentSection.copy(slides = slides)
                }
            }
        )

    override fun undo(song: Song): Song =
        song.copy(
            sections = song.sections.map { currentSection ->
                if (currentSection != section) {
                    currentSection
                } else {
                    val slides = currentSection.slides.toMutableList()
                    val duplicateIndex = originalIndex + 1

                    if (duplicateIndex in slides.indices) {
                        slides.removeAt(duplicateIndex)
                    }
                    currentSection.copy(slides = slides)
                }
            }
        )
}
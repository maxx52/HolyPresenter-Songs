package org.holypresenter_songs.domain.editor.command.slide

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class DeleteSlideCommand(
    private val section: SongSection,
    private val slide: SongSlide
) : SongEditCommand {
    private val deletedIndex: Int =
        section.slides.indexOf(slide)

    override val description: String = "Удалить слайд"

    override fun execute(song: Song): Song =
        song.copy(
            sections = song.sections.mapNotNull { currentSection ->
                if (currentSection != section) {
                    currentSection
                } else {
                    val updatedSlides = currentSection.slides - slide

                    if (updatedSlides.isEmpty()) {
                        null
                    } else {
                        currentSection.copy(slides = updatedSlides)
                    }
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
                    val safeIndex = deletedIndex.coerceIn(0, slides.size)

                    slides.add(safeIndex, slide)

                    currentSection.copy(slides = slides)
                }
            }
        )
}
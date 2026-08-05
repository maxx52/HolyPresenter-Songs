package org.holypresenter_songs.domain.editor.command.slide

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class MergeSlideWithPreviousCommand(
    section: SongSection,
    currentSlide: SongSlide
) : SongEditCommand {
    override val description: String = "Объединить с предыдущим слайдом"
    private val sectionId = section.id
    private val beforeSlides = section.slides

    private val currentSlideIndex =
        beforeSlides.indexOfFirst {
            it.id == currentSlide.id
        }

    private val previousSlide =
        beforeSlides.getOrNull(
            currentSlideIndex - 1
        )

    val mergedSlideId = previousSlide?.id

    private val afterSlides =
        if (previousSlide == null) {
            beforeSlides
        } else {
            beforeSlides
                .toMutableList()
                .apply {
                    val mergedChords = (
                            previousSlide.alignedChords() + currentSlide.alignedChords()
                        ).compactChordLines()

                    this[currentSlideIndex - 1] =
                        previousSlide.copy(
                            lines = previousSlide.lines + currentSlide.lines,
                            chords = mergedChords
                        )
                    removeAt(currentSlideIndex)
                }.toList()
        }

    override fun execute(song: Song): Song =
        song.replaceSectionSlides(
            sectionId = sectionId,
            expectedSlides = beforeSlides,
            replacementSlides = afterSlides
        )

    override fun undo(song: Song): Song =
        song.replaceSectionSlides(
            sectionId = sectionId,
            expectedSlides = afterSlides,
            replacementSlides = beforeSlides
        )
}
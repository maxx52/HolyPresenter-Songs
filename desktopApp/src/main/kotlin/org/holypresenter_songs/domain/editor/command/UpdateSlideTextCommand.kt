package org.holypresenter_songs.domain.editor.command

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide

class UpdateSlideTextCommand(
    private val section: SongSection,
    private val slide: SongSlide,
    oldText: String,
    newText: String
) : SongEditCommand {
    override val description: String = "Изменить текст слайда"
    private val oldLines = oldText.lines()
    private val newLines = newText.lines()
    private val oldChords = slide.chords

    private val newChords =
        resizeChords(
            chords = oldChords,
            lineCount = newLines.size
        )

    override fun execute(
        song: Song
    ): Song =
        updateSlide(
            song = song,
            lines = newLines,
            chords = newChords
        )

    override fun undo(
        song: Song
    ): Song =
        updateSlide(
            song = song,
            lines = oldLines,
            chords = oldChords
        )

    private fun updateSlide(
        song: Song,
        lines: List<String>,
        chords: List<String?>
    ): Song =
        song.copy(
            sections = song.sections.map {
                    currentSection ->
                if (currentSection.id != section.id) {
                    currentSection
                } else {
                    currentSection.copy(
                        slides = currentSection.slides.map {
                                    currentSlide ->
                                if (currentSlide.id != slide.id) {
                                    currentSlide
                                } else {
                                    currentSlide.copy(
                                        lines = lines,
                                        chords = chords
                                    )
                                }
                            }
                    )
                }
            }
        )

    private fun resizeChords(
        chords: List<String?>,
        lineCount: Int
    ): List<String?> {
        if (chords.isEmpty()) {
            return emptyList()
        }

        return List(lineCount) { index ->
            chords.getOrNull(index)
        }
    }
}
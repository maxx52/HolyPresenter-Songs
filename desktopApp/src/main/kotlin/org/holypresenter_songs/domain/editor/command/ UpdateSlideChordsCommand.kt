package org.holypresenter_songs.domain.editor.command

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide

class UpdateSlideChordsCommand(
    private val section: SongSection,
    private val slide: SongSlide,
    newText: String
) : SongEditCommand {
    override val description: String = "Изменить аккорды слайда"
    private val oldChords = slide.chords

    private val newChords =
        parseChords(
            text = newText,
            lineCount = slide.lines.size
        )

    override fun execute(
        song: Song
    ): Song =
        updateSlide(
            song = song,
            chords = newChords
        )

    override fun undo(
        song: Song
    ): Song =
        updateSlide(
            song = song,
            chords = oldChords
        )

    private fun updateSlide(
        song: Song,
        chords: List<String?>
    ): Song =
        song.copy(
            sections = song.sections.map {
                    currentSection ->

                if (currentSection.id != section.id) {
                    currentSection
                } else {
                    currentSection.copy(
                        slides =
                            currentSection.slides.map {
                                    currentSlide ->

                                if (
                                    currentSlide.id != slide.id
                                ) {
                                    currentSlide
                                } else {
                                    currentSlide.copy(
                                        chords = chords
                                    )
                                }
                            }
                    )
                }
            }
        )

    private fun parseChords(
        text: String,
        lineCount: Int
    ): List<String?> {
        if (lineCount == 0) {
            return emptyList()
        }

        val sourceLines =
            text
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .lines()

        val chords =
            List(lineCount) { index ->
                sourceLines
                    .getOrNull(index)
                    ?.trimEnd()
                    ?.takeIf { it.isNotBlank() }
            }

        return if (
            chords.all { it == null }
        ) {
            emptyList()
        } else {
            chords
        }
    }
}
package org.holypresenter_songs.domain.editor.command.slide

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class SplitSlideCommand(
    private val section: SongSection,
    private val slide: SongSlide,
    splitOffset: Int
) : SongEditCommand {
    override val description: String = "Разделить слайд"
    private val originalSlide = slide

    private val originalLines =
        slide.lines.ifEmpty {
            listOf("")
        }

    private val originalText = originalLines.joinToString("\n")

    private val normalizedOffset =
        splitOffset.coerceIn(
            minimumValue = 0,
            maximumValue = originalText.length
        )

    private val alignedChords =
        List(originalLines.size) { index ->
            slide.chords.getOrNull(index)
        }

    private val splitResult = splitContent()

    private val firstSlide =
        slide.copy(
            lines = splitResult.firstLines,
            chords = compactChords(
                splitResult.firstChords
            )
        )

    val secondSlide =
        SongSlide(
            lines = splitResult.secondLines,
            chords = compactChords(
                splitResult.secondChords
            )
        )

    override fun execute(
        song: Song
    ): Song {
        val sectionIndex =
            song.sections.indexOfFirst {
                it.id == section.id
            }

        if (sectionIndex == -1) {
            return song
        }

        val sections = song.sections.toMutableList()
        val currentSection = sections[sectionIndex]

        val slideIndex =
            currentSection.slides.indexOfFirst {
                it.id == slide.id
            }

        if (slideIndex == -1) {
            return song
        }

        val slides = currentSection.slides.toMutableList()

        slides[slideIndex] = firstSlide

        /*
         * При повторном выполнении команды
         * не добавляем второй слайд дважды.
         */
        if (
            slides.none {
                it.id == secondSlide.id
            }
        ) {
            slides.add(
                index = slideIndex + 1,
                element = secondSlide
            )
        }

        sections[sectionIndex] =
            currentSection.copy(
                slides = slides
            )

        return song.copy(
            sections = sections
        )
    }

    override fun undo(
        song: Song
    ): Song {
        val sectionIndex =
            song.sections.indexOfFirst {
                it.id == section.id
            }

        if (sectionIndex == -1) {
            return song
        }

        val sections = song.sections.toMutableList()
        val currentSection = sections[sectionIndex]
        val slides = currentSection.slides.toMutableList()

        val firstSlideIndex =
            slides.indexOfFirst {
                it.id == originalSlide.id
            }

        if (firstSlideIndex == -1) {
            return song
        }

        /*
         * Возвращаем исходный слайд
         * вместе с его текстом и аккордами.
         */
        slides[firstSlideIndex] =
            originalSlide

        val secondSlideIndex =
            slides.indexOfFirst {
                it.id == secondSlide.id
            }

        if (secondSlideIndex != -1) {
            slides.removeAt(secondSlideIndex)
        }

        sections[sectionIndex] =
            currentSection.copy(
                slides = slides
            )

        return song.copy(
            sections = sections
        )
    }

    private fun splitContent(): SplitResult {
        val textBeforeCursor =
            originalText.substring(
                startIndex = 0,
                endIndex = normalizedOffset
            )

        val lineIndex =
            textBeforeCursor
                .count { character ->
                    character == '\n'
                }
                .coerceIn(originalLines.indices)

        val lineStartOffset =
            textBeforeCursor
                .lastIndexOf('\n')
                .plus(1)

        val currentLine = originalLines[lineIndex]

        val columnIndex =
            (normalizedOffset - lineStartOffset)
                .coerceIn(
                    minimumValue = 0,
                    maximumValue = currentLine.length
                )

        val leftPart =
            currentLine.substring(
                startIndex = 0,
                endIndex = columnIndex
            )

        val rightPart =
            currentLine.substring(
                startIndex = columnIndex
            )

        val currentChord = alignedChords.getOrNull(lineIndex)

        val firstLines =
            when {
                /*
                 * Разделение в самом начале:
                 * первый слайд будет пустым.
                 */
                normalizedOffset == 0 ->
                    listOf("")

                /*
                 * Курсор находится в начале
                 * одной из последующих строк.
                 */
                columnIndex == 0 ->
                    originalLines.take(lineIndex)

                else ->
                    originalLines.take(lineIndex) + leftPart
            }

        val secondLines =
            when {
                /*
                 * Разделение в самом конце:
                 * второй слайд будет пустым.
                 */
                normalizedOffset ==
                        originalText.length ->
                    listOf("")

                /*
                 * Курсор находится в конце строки.
                 * Новая пустая строка не создаётся.
                 */
                columnIndex ==
                        currentLine.length ->
                    originalLines.drop(
                        lineIndex + 1
                    )

                else ->
                    listOf(rightPart) +
                            originalLines.drop(
                                lineIndex + 1
                            )
            }

        val firstChords =
            when {
                normalizedOffset == 0 ->
                    listOf(null)

                columnIndex == 0 ->
                    alignedChords.take(lineIndex)

                else ->
                    alignedChords.take(lineIndex) + currentChord
            }

        val secondChords =
            when {
                normalizedOffset ==
                        originalText.length ->
                    listOf(null)

                columnIndex ==
                        currentLine.length ->
                    alignedChords.drop(
                        lineIndex + 1
                    )

                columnIndex == 0 ->
                    listOf(currentChord) +
                        alignedChords.drop(
                            lineIndex + 1
                        )
                /*
                 * При разделении посередине строки
                 * её аккорды остаются у первой части.
                 */
                else ->
                    listOf(null) + alignedChords.drop(
                    lineIndex + 1
                    )
            }

        return SplitResult(
            firstLines = firstLines,
            firstChords = firstChords,
            secondLines = secondLines,
            secondChords = secondChords
        )
    }

    private fun compactChords(
        chords: List<String?>
    ): List<String?> =
        if (
            chords.all {
                it.isNullOrBlank()
            }
        ) {
            emptyList()
        } else {
            chords
        }

    private data class SplitResult(
        val firstLines: List<String>,
        val firstChords: List<String?>,
        val secondLines: List<String>,
        val secondChords: List<String?>
    )
}
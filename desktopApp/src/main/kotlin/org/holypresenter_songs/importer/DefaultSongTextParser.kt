package org.holypresenter_songs.importer

import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSectionType
import org.holypresenter_songs.domain.SongSlide

internal class DefaultSongTextParser :
    SongTextParser {

    override fun parse(
        text: String,
        maxLinesPerSlide: Int
    ): SongImportDraft {
        require(maxLinesPerSlide > 0) {
            "maxLinesPerSlide must be greater than zero"
        }

        val normalizedText =
            text
                .replace("\r\n", "\n")
                .replace('\r', '\n')

        if (normalizedText.isBlank()) {
            return SongImportDraft()
        }

        val parsedSections = mutableListOf<MutableSection>()
        val sectionCounters = mutableMapOf<SongSectionType, Int>()
        var pendingChordLine: String? = null

        fun startSection(
            header: SectionHeader
        ) {
            val currentNumber = sectionCounters[header.type] ?: 0

            val sectionNumber =
                header.number
                    ?.takeIf { it > 0 }
                    ?: (currentNumber + 1)

            sectionCounters[header.type] =
                maxOf(
                    currentNumber,
                    sectionNumber
                )

            parsedSections +=
                MutableSection(
                    type = header.type,
                    number = sectionNumber
                )
        }

        fun ensureSection() {
            if (parsedSections.isNotEmpty()) {
                return
            }

            startSection(
                SectionHeader(
                    type = SongSectionType.VERSE,
                    number = 1
                )
            )
        }

        fun appendPendingChordAsInstrumental() {
            val chordLine = pendingChordLine ?: return

            ensureSection()

            parsedSections
                .last()
                .lines
                .add(
                    ParsedSongLine(
                        text = "",
                        chords = chordLine
                    )
                )
            pendingChordLine = null
        }

        fun appendParagraphBreak() {
            if (parsedSections.isEmpty()) {
                return
            }

            val currentLines = parsedSections.last().lines

            if (
                currentLines.isNotEmpty() &&
                currentLines.last() != null
            ) {
                currentLines.add(null)
            }
        }

        normalizedText
            .lines()
            .forEach { rawLine ->
                /*
                 * Для аккордов сохраняем начальные пробелы:
                 * они могут использоваться для выравнивания
                 * аккордов относительно слов.
                 */
                val line = rawLine.trimEnd()
                val trimmedLine = line.trim()
                val header = parseSectionHeader(trimmedLine)

                if (header != null) {
                    appendPendingChordAsInstrumental()
                    startSection(header)
                    return@forEach
                }

                if (trimmedLine.isBlank()) {
                    appendPendingChordAsInstrumental()
                    appendParagraphBreak()
                    return@forEach
                }

                if (isChordLine(trimmedLine)) {
                    /*
                     * Если подряд встретились две строки
                     * аккордов, предыдущую сохраняем как
                     * самостоятельную инструментальную строку.
                     */
                    if (pendingChordLine != null) {
                        appendPendingChordAsInstrumental()
                    }

                    ensureSection()
                    pendingChordLine = line
                    return@forEach
                }
                ensureSection()

                parsedSections
                    .last()
                    .lines
                    .add(
                        ParsedSongLine(
                            text = trimmedLine,
                            chords = pendingChordLine
                        )
                    )
                pendingChordLine = null
            }
        /**
         * Сохраняем последнюю аккордную строку,
         * если после неё не было строки текста.
         */
        appendPendingChordAsInstrumental()

        val sections =
            parsedSections.mapNotNull { section ->
                val slides =
                    splitIntoSlides(
                        lines = section.lines,
                        maxLinesPerSlide = maxLinesPerSlide
                    ).map { slideLines ->
                        createSongSlide(
                            lines = slideLines
                        )
                    }

                if (slides.isEmpty()) {
                    null
                } else {
                    SongSection(
                        type = section.type,
                        number = section.number,
                        slides = slides
                    )
                }
            }

        return SongImportDraft(
            sections = sections
        )
    }

    private fun createSongSlide(
        lines: List<ParsedSongLine>
    ): SongSlide {
        val lyricsLines =
            lines.map { line ->
                line.text
            }

        val chordLines =
            lines.map { line ->
                line.chords
            }

        val containsChords =
            chordLines.any { chords ->
                chords != null
            }

        return SongSlide(
            lines = lyricsLines,
            chords = if (containsChords) {
                chordLines
            } else {
                emptyList()
            }
        )
    }

    private fun parseSectionHeader(
        line: String
    ): SectionHeader? {
        if (line.isBlank()) {
            return null
        }

        val cleanedLine =
            line
                .trim()
                .trim('[', ']', '(', ')')
                .removeSuffix(":")
                .trim()

        val number =
            NUMBER_REGEX
                .find(cleanedLine)
                ?.value
                ?.toIntOrNull()

        val normalizedName =
            cleanedLine
                .lowercase()
                .replace('ё', 'е')
                .replace(
                    NUMBER_WITH_SIGN_REGEX,
                    " "
                )
                .replace(
                    NUMBER_REGEX,
                    " "
                )
                .replace(
                    SEPARATOR_REGEX,
                    " "
                )
                .replace(
                    MULTIPLE_SPACES_REGEX,
                    " "
                )
                .trim()

        val type =
            when (normalizedName) {
                "куплет",
                "verse",
                "стих" ->
                    SongSectionType.VERSE

                "припев",
                "chorus",
                "рефрен" ->
                    SongSectionType.CHORUS

                "предприпев",
                "пред припев",
                "prechorus",
                "pre chorus" ->
                    SongSectionType.PRE_CHORUS

                "бридж",
                "мост",
                "bridge" ->
                    SongSectionType.BRIDGE

                "тег",
                "tag" ->
                    SongSectionType.TAG

                "вступление",
                "интро",
                "intro",
                "проигрыш",
                "instrumental" ->
                    SongSectionType.INTRO

                "концовка",
                "окончание",
                "финал",
                "аутро",
                "ending",
                "outro" ->
                    SongSectionType.ENDING

                else -> null
            }

        return type?.let {
            SectionHeader(
                type = it,
                number = number
            )
        }
    }

    private fun isChordLine(
        line: String
    ): Boolean {
        val tokens =
            line
                .replace("|", " ")
                .split(MULTIPLE_SPACES_REGEX)
                .map { token ->
                    token.trim(',', ';', ':', '.', '(', ')', '[', ']')
                }
                .filter { token ->
                    token.isNotBlank()
                }

        if (tokens.isEmpty()) {
            return false
        }

        return tokens.all { token ->
            CHORD_TOKEN_REGEX.matches(token) ||
                    CHORD_CONTROL_TOKEN_REGEX.matches(token)
        }
    }

    private fun splitIntoSlides(
        lines: List<ParsedSongLine?>,
        maxLinesPerSlide: Int
    ): List<List<ParsedSongLine>> {
        val slides = mutableListOf<List<ParsedSongLine>>()
        val currentSlide = mutableListOf<ParsedSongLine>()

        var lyricsLineCount = 0

        fun flushSlide() {
            if (currentSlide.isEmpty()) {
                return
            }

            slides += currentSlide.toList()
            currentSlide.clear()
            lyricsLineCount = 0
        }

        lines.forEach { parsedLine ->
            if (parsedLine == null) {
                flushSlide()
                return@forEach
            }

            val isLyricsLine = parsedLine.text.isNotBlank()

            val isInstrumentalLine =
                parsedLine.text.isBlank() &&
                        parsedLine.chords != null

            /**
             * Аккордная строка не увеличивает
             * количество строк текста на слайде.
             *
             * Но если лимит текста уже достигнут,
             * инструментальную строку переносим
             * на следующий слайд.
             */
            if (
                lyricsLineCount >= maxLinesPerSlide &&
                (isLyricsLine || isInstrumentalLine)
            ) {
                flushSlide()
            }

            currentSlide += parsedLine

            if (isLyricsLine) {
                lyricsLineCount++
            }
        }
        flushSlide()
        return slides
    }

    private data class SectionHeader(
        val type: SongSectionType,
        val number: Int?
    )

    private data class ParsedSongLine(
        val text: String,
        val chords: String?
    )

    private data class MutableSection(
        val type: SongSectionType,
        val number: Int,
        /*
         * null обозначает разрыв между
         * абзацами или группами слайдов.
         */
        val lines: MutableList<ParsedSongLine?> = mutableListOf()
    )

    private companion object {
        val NUMBER_REGEX = Regex("""\d+""")
        val NUMBER_WITH_SIGN_REGEX = Regex("""№\s*\d+""")
        val SEPARATOR_REGEX = Regex("""[._:/\\—–-]+""")
        val MULTIPLE_SPACES_REGEX = Regex("""\s+""")

        val CHORD_TOKEN_REGEX =
            Regex(
                pattern =
                    """
                    ^[A-H]
                    (?:\#|b)?
                    (?:m|maj|min|dim|aug|sus|add)?
                    \d*
                    (?:sus\d*|add\d*)?
                    (?:/(?:[A-H](?:\#|b)?|\d+))?
                    $
                    """.trimIndent()
                        .replace("\n", "")
                        .replace(" ", ""),
                option = RegexOption.IGNORE_CASE
            )

        val CHORD_CONTROL_TOKEN_REGEX =
            Regex(
                pattern =
                    """^(?:x\d+|\d+x|×\d+|%|n\.?c\.?)$""",
                option = RegexOption.IGNORE_CASE
            )
    }
}
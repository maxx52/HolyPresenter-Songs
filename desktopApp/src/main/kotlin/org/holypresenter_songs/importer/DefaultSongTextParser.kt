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

        val normalizedText = text
            .replace("\r\n", "\n")
            .replace('\r', '\n')

        if (normalizedText.isBlank()) {
            return SongImportDraft()
        }

        val parsedSections = mutableListOf<MutableSection>()
        val sectionCounters = mutableMapOf<SongSectionType, Int>()

        fun startSection(
            header: SectionHeader
        ) {
            val currentNumber =
                sectionCounters[header.type] ?: 0

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

        normalizedText
            .lines()
            .forEach { rawLine ->
                val line = rawLine.trim()
                val header = parseSectionHeader(line)

                if (header != null) {
                    startSection(header)
                    return@forEach
                }

                if (
                    line.isNotBlank() &&
                    isChordLine(line)
                ) {
                    return@forEach
                }

                if (parsedSections.isEmpty()) {
                    if (line.isBlank()) {
                        return@forEach
                    }

                    startSection(
                        SectionHeader(
                            type =
                                SongSectionType.VERSE,
                            number = 1
                        )
                    )
                }

                parsedSections
                    .last()
                    .lines
                    .add(line)
            }

        val sections =
            parsedSections.mapNotNull { section ->
                val slides =
                    splitIntoSlides(
                        lines = section.lines,
                        maxLinesPerSlide =
                            maxLinesPerSlide
                    ).map { lines ->
                        SongSlide(
                            lines = lines
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

    private fun parseSectionHeader(
        line: String
    ): SectionHeader? {
        if (line.isBlank()) {
            return null
        }

        val cleanedLine =
            line
                .trim()
                .trim(
                    '[',
                    ']',
                    '(',
                    ')'
                )
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
                "intro" ->
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
                    token.trim(
                        ',',
                        ';',
                        ':',
                        '.',
                        '(',
                        ')',
                        '[',
                        ']'
                    )
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
        lines: List<String>,
        maxLinesPerSlide: Int
    ): List<List<String>> {
        val slides = mutableListOf<List<String>>()
        val paragraph = mutableListOf<String>()

        fun flushParagraph() {
            if (paragraph.isEmpty()) {
                return
            }

            paragraph
                .chunked(maxLinesPerSlide)
                .forEach { slideLines ->
                    slides += slideLines
                }
            paragraph.clear()
        }

        lines.forEach { line ->
            if (line.isBlank()) {
                flushParagraph()
            } else {
                paragraph += line.trim()
            }
        }
        flushParagraph()
        return slides
    }

    private data class SectionHeader(
        val type: SongSectionType,
        val number: Int?
    )

    private data class MutableSection(
        val type: SongSectionType,
        val number: Int,
        val lines: MutableList<String> = mutableListOf()
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
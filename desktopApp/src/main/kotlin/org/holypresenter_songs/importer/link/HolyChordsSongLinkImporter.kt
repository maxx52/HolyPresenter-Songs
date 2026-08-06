package org.holypresenter_songs.importer.link

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.StringReader
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import javax.swing.text.MutableAttributeSet
import javax.swing.text.html.HTML
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.html.parser.ParserDelegator

internal class HolyChordsSongLinkImporter(
    private val httpClient: HttpClient =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(
                HttpClient.Redirect.NORMAL
            )
            .build()
) : SongLinkImporter {

    override fun supports(
        url: String
    ): Boolean =
        normalizeUri(url)
            ?.let(::isSupportedUri)
            ?: false

    override suspend fun importSong(
        url: String
    ): SongLinkImportResult =
        withContext(Dispatchers.IO) {
            val uri =
                normalizeUri(url)
                    ?: return@withContext failure(
                        "Введите корректную ссылку HolyChords."
                    )

            if (!isSupportedUri(uri)) {
                return@withContext failure(
                    "Поддерживаются только ссылки holychords.pro и holychords.com."
                )
            }

            try {
                val request =
                    HttpRequest.newBuilder(uri)
                        .timeout(Duration.ofSeconds(20))
                        .header(
                            "User-Agent",
                            "HolyPresenter/1.0"
                        )
                        .header(
                            "Accept",
                            "text/html,application/xhtml+xml"
                        )
                        .GET()
                        .build()

                val response =
                    httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString(
                            StandardCharsets.UTF_8
                        )
                    )

                if (!isSupportedUri(response.uri())) {
                    return@withContext failure(
                        "HolyChords перенаправил запрос на неподдерживаемый сайт."
                    )
                }

                if (
                    response.statusCode() !in
                    200..299
                ) {
                    return@withContext failure(
                        when (response.statusCode()) {
                            404 -> "Песня по этой ссылке не найдена."
                            else -> "HolyChords вернул ошибку ${response.statusCode()}."
                        }
                    )
                }

                parsePage(
                    html = response.body(),
                    sourceUrl = response.uri().toString()
                )
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                failure("Не удалось загрузить страницу HolyChords. Проверьте подключение к интернету.")
            }
        }

    private fun parsePage(
        html: String,
        sourceUrl: String
    ): SongLinkImportResult {
        val callback = HolyChordsHtmlCallback()

        runCatching {
            StringReader(html).use { reader ->
                ParserDelegator().parse(
                    reader,
                    callback,
                    true
                )
            }
        }.getOrElse {
            return failure("Не удалось прочитать страницу HolyChords.")
        }

        val title =
            callback.title
                ?.normalizeInline()
                ?.takeIf(String::isNotBlank)
                ?: return failure("На странице не найдено название песни.")

        val author =
            callback.author
                ?.normalizeInline()
                ?.takeIf(String::isNotBlank)

        val rawSongText =
            callback.preformattedBlocks
                .maxByOrNull(String::length)
                ?: return failure("На странице не найден текст песни.")

        val sourceText =
            cleanSourceText(
                rawText = rawSongText,
                title = title,
                author = author,
                sourceUrl = sourceUrl
            )

        if (sourceText.isBlank()) {
            return failure("HolyChords вернул пустой текст песни.")
        }

        return SongLinkImportResult.Success(
            SongLinkImportData(
                title = title,
                author = author,
                sourceText = sourceText
            )
        )
    }

    private fun cleanSourceText(
        rawText: String,
        title: String,
        author: String?,
        sourceUrl: String
    ): String {
        val lines =
            rawText
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replace('\u00A0', ' ')
                .lines()
                .map(String::trimEnd)
                .toMutableList()

        removeBlankEdges(lines)

        /*
         * HolyChords добавляет в начало копируемого
         * текста название и исполнителя.
         * В поля диалога они попадут отдельно.
         */
        while (
            lines.firstOrNull()?.let { line ->
                isMetadataLine(
                    line = line,
                    title = title,
                    author = author
                )
            } == true
        ) {
            lines.removeAt(0)
            removeLeadingBlankLines(lines)
        }

        /*
         * В конец копируемого блока сайт может
         * добавлять исходную ссылку.
         */
        while (
            lines.lastOrNull()?.let { line ->
                line.isBlank() ||
                        isHolyChordsUrl(
                            line = line,
                            sourceUrl = sourceUrl
                        )
            } == true
        ) {
            lines.removeAt(lines.lastIndex)
        }
        return lines.joinToString("\n")
    }

    private fun isMetadataLine(
        line: String,
        title: String,
        author: String?
    ): Boolean {
        val normalizedLine = line.normalizeInline()

        val candidates =
            buildList {
                add(title.normalizeInline())
                author
                    ?.normalizeInline()
                    ?.takeIf(String::isNotBlank)
                    ?.let { normalizedAuthor ->
                        add(normalizedAuthor)

                        add(
                            "$title $normalizedAuthor"
                                .normalizeInline()
                        )

                        add(
                            "$normalizedAuthor $title"
                                .normalizeInline()
                        )
                    }
            }

        return candidates.any { candidate ->
            normalizedLine.equals(
                candidate,
                ignoreCase = true
            )
        }
    }

    private fun isHolyChordsUrl(
        line: String,
        sourceUrl: String
    ): Boolean {
        val normalizedLine = line.trim().removeSuffix("/")
        val normalizedSourceUrl = sourceUrl.trim().removeSuffix("/")

        if (
            normalizedLine.equals(
                normalizedSourceUrl,
                ignoreCase = true
            )
        ) {
            return true
        }
        return HOLY_CHORDS_URL_REGEX.matches(normalizedLine)
    }

    private fun removeBlankEdges(
        lines: MutableList<String>
    ) {
        removeLeadingBlankLines(lines)

        while (
            lines.lastOrNull()?.isBlank() == true
        ) {
            lines.removeAt(lines.lastIndex)
        }
    }

    private fun removeLeadingBlankLines(
        lines: MutableList<String>
    ) {
        while (
            lines.firstOrNull()?.isBlank() == true
        ) {
            lines.removeAt(0)
        }
    }

    private fun normalizeUri(
        value: String
    ): URI? {
        val trimmed = value.trim()

        if (trimmed.isEmpty()) {
            return null
        }

        val absoluteValue =
            if ("://" in trimmed) {
                trimmed
            } else {
                "https://$trimmed"
            }

        return runCatching {
            URI(absoluteValue)
        }.getOrNull()
    }

    private fun isSupportedUri(
        uri: URI
    ): Boolean {
        val scheme = uri.scheme
            ?.lowercase()
            ?: return false

        val host = uri.host
            ?.lowercase()
            ?: return false

        val supportedPort =
            uri.port == -1 ||
                    uri.port == 80 ||
                    uri.port == 443

        return scheme in SUPPORTED_SCHEMES &&
                host in SUPPORTED_HOSTS &&
                uri.userInfo == null &&
                supportedPort
    }

    private fun failure(
        message: String
    ): SongLinkImportResult.Failure =
        SongLinkImportResult.Failure(
            message = message
        )

    private class HolyChordsHtmlCallback :
        HTMLEditorKit.ParserCallback() {
        private val titleBuilder = StringBuilder()
        private val authorBuilder = StringBuilder()
        private var captureTitle = false
        private var titleCaptured = false
        private var captureAuthor = false
        private var authorCaptured = false
        private var currentPreformattedBlock: StringBuilder? = null
        private var ignoredTagDepth = 0
        val preformattedBlocks = mutableListOf<String>()

        val title: String?
            get() =
                titleBuilder
                    .toString()
                    .takeIf(String::isNotBlank)

        val author: String?
            get() =
                authorBuilder
                    .toString()
                    .takeIf(String::isNotBlank)

        override fun handleStartTag(
            tag: HTML.Tag,
            attributes: MutableAttributeSet,
            position: Int
        ) {
            if (ignoredTagDepth > 0) {
                ignoredTagDepth++
                return
            }

            if (attributes.hasCssClass("d-none")) {
                ignoredTagDepth = 1
                return
            }

            when (tag) {
                HTML.Tag.H2 -> {
                    if (!titleCaptured) {
                        captureTitle = true
                    }
                }

                HTML.Tag.H5 -> {
                    if (
                        titleCaptured && !authorCaptured
                    ) {
                        captureAuthor = true
                    }
                }

                HTML.Tag.PRE -> {
                    currentPreformattedBlock = StringBuilder()
                }
            }
        }

        override fun handleSimpleTag(
            tag: HTML.Tag,
            attributes: MutableAttributeSet,
            position: Int
        ) {
            if (
                ignoredTagDepth > 0 ||
                attributes.hasCssClass("d-none")
            ) {
                return
            }

            if (
                tag == HTML.Tag.BR &&
                currentPreformattedBlock != null
            ) {
                currentPreformattedBlock?.append('\n')
            }
        }

        override fun handleEndTag(
            tag: HTML.Tag,
            position: Int
        ) {
            if (ignoredTagDepth > 0) {
                ignoredTagDepth--
                return
            }

            when (tag) {
                HTML.Tag.H2 -> {
                    if (captureTitle) {
                        captureTitle = false
                        titleCaptured = titleBuilder.isNotBlank()
                    }
                }

                HTML.Tag.H5 -> {
                    if (captureAuthor) {
                        captureAuthor = false
                        authorCaptured = authorBuilder.isNotBlank()
                    }
                }

                HTML.Tag.PRE -> {
                    currentPreformattedBlock
                        ?.toString()
                        ?.takeIf(String::isNotBlank)
                        ?.let(preformattedBlocks::add)

                    currentPreformattedBlock = null
                }
            }
        }

        override fun handleText(
            data: CharArray,
            position: Int
        ) {
            if (ignoredTagDepth > 0) {
                return
            }

            val text = String(data)

            if (captureTitle) {
                titleBuilder.append(text)
            }

            if (captureAuthor) {
                authorBuilder.append(text)
            }

            currentPreformattedBlock?.append(text)
        }

        private fun MutableAttributeSet.hasCssClass(
            className: String
        ): Boolean {
            val classes =
                getAttribute(
                    HTML.Attribute.CLASS
                )
                    ?.toString()
                    ?.split(CSS_CLASS_SEPARATOR_REGEX)
                    .orEmpty()

            return classes.any { cssClass ->
                cssClass.equals(
                    className,
                    ignoreCase = true
                )
            }
        }

        private companion object {
            val CSS_CLASS_SEPARATOR_REGEX =
                Regex("""\s+""")
        }
    }

    private fun String.normalizeInline(): String =
        replace('\u00A0', ' ')
            .replace(
                INLINE_WHITESPACE_REGEX,
                " "
            )
            .trim()

    private companion object {
        val SUPPORTED_SCHEMES = setOf("http", "https")

        val SUPPORTED_HOSTS =
            setOf(
                "holychords.pro",
                "www.holychords.pro",
                "holychords.com",
                "www.holychords.com"
            )

        val INLINE_WHITESPACE_REGEX =
            Regex("""\s+""")

        val HOLY_CHORDS_URL_REGEX =
            Regex(
                pattern =
                    """https?://(?:www\.)?""" +
                            """holychords\.(?:pro|com)/\S+""",
                option = RegexOption.IGNORE_CASE
            )
    }
}
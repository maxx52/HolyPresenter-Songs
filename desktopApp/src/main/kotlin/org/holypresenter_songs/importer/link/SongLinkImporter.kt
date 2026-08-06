package org.holypresenter_songs.importer.link

internal interface SongLinkImporter {
    /**
     * Проверяет, поддерживается ли ссылка
     * конкретным импортёром.
     */
    fun supports(
        url: String
    ): Boolean

    /**
     * Загружает данные песни по ссылке.
     *
     * Разбор текста на секции и слайды
     * выполняет существующий SongTextParser.
     */
    suspend fun importSong(
        url: String
    ): SongLinkImportResult
}

internal sealed interface SongLinkImportResult {
    data class Success(
        val data: SongLinkImportData
    ) : SongLinkImportResult

    data class Failure(
        val message: String
    ) : SongLinkImportResult
}

internal data class SongLinkImportData(
    val title: String,
    val author: String?,
    val sourceText: String
) {
    init {
        require(title.isNotBlank()) {
            "Imported song title must not be blank"
        }

        require(sourceText.isNotBlank()) {
            "Imported song text must not be blank"
        }
    }
}
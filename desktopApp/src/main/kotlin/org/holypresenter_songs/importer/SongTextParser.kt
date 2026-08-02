package org.holypresenter_songs.importer

internal interface SongTextParser {
    fun parse(
        text: String,
        maxLinesPerSlide: Int = DEFAULT_MAX_LINES_PER_SLIDE
    ): SongImportDraft

    companion object {
        const val DEFAULT_MAX_LINES_PER_SLIDE = 2
    }
}
package org.holypresenter_songs.importer

import org.holypresenter_songs.domain.SongSection

data class SongImportDraft(
    val title: String = "",
    val author: String = "",
    val sections: List<SongSection> = emptyList()
) {
    val isEmpty: Boolean
        get() = sections.isEmpty()
}
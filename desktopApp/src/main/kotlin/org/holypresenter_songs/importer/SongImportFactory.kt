package org.holypresenter_songs.importer

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongId
import org.holypresenter_songs.domain.SongMetadata

internal object SongImportFactory {
    fun create(
        draft: SongImportDraft
    ): Song {
        require(draft.title.isNotBlank()) {
            "Song title must not be blank"
        }

        require(draft.sections.isNotEmpty()) {
            "Imported song must contain sections"
        }

        val normalizedAuthor =
            draft.author
                .trim()
                .takeIf { it.isNotEmpty() }

        return Song(
            id = SongId.random(),
            metadata = SongMetadata(
                title = draft.title.trim(),
                author = normalizedAuthor
            ),
            sections = draft.sections
        )
    }
}
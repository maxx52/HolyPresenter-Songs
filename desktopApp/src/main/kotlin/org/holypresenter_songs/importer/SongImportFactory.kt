package org.holypresenter_songs.importer

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongId
import org.holypresenter_songs.domain.SongMetadata
import org.holypresenter_songs.domain.SongOrderEntry

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
                .takeIf(String::isNotEmpty)

        val sections = draft.sections

        return Song(
            id = SongId.random(),
            metadata = SongMetadata(
                title = draft.title.trim(),
                author = normalizedAuthor
            ),
            sections = sections,
            executionOrder =
                sections.map { section ->
                    SongOrderEntry(
                        sectionId = section.id
                    )
                }
        )
    }
}
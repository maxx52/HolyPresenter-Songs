package org.holypresenter_songs.domain.editor.command.section

import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongOrderEntry
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.editor.command.SongEditCommand

class DeleteSectionCommand(
    private val section: SongSection
) : SongEditCommand {
    override val description: String = "Удалить секцию"
    private var originalSectionIndex: Int? = null
    private var removedOrderEntries: List<IndexedOrderEntry>? = null

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

        if (originalSectionIndex == null) {
            originalSectionIndex = sectionIndex
        }

        if (removedOrderEntries == null) {
            removedOrderEntries =
                song.executionOrder
                    .mapIndexedNotNull {
                        index,
                        entry ->
                        entry
                            .takeIf {
                                it.sectionId == section.id
                            }
                            ?.let {
                                IndexedOrderEntry(
                                    index = index,
                                    entry = it
                                )
                            }
                    }
        }

        val updatedSections = song.sections.toMutableList()

        updatedSections.removeAt(sectionIndex)

        val updatedOrder =
            song.executionOrder.filterNot {
                it.sectionId == section.id
            }

        return song.copy(
            sections = updatedSections,
            executionOrder = updatedOrder
        )
    }

    override fun undo(
        song: Song
    ): Song {
        if (
            song.sections.any {
                it.id == section.id
            }
        ) {
            return song
        }

        val updatedSections = song.sections.toMutableList()

        val sectionInsertIndex =
            originalSectionIndex
                ?.coerceIn(
                    minimumValue = 0,
                    maximumValue = updatedSections.size
                )
                ?: updatedSections.size

        updatedSections.add(
            index = sectionInsertIndex,
            element = section
        )

        val updatedOrder = song.executionOrder.toMutableList()

        removedOrderEntries
            .orEmpty()
            .sortedBy { indexedEntry ->
                indexedEntry.index
            }
            .forEach { indexedEntry ->
                val entryAlreadyExists =
                    updatedOrder.any {
                        it.id == indexedEntry.entry.id
                    }

                if (!entryAlreadyExists) {
                    val insertIndex =
                        indexedEntry.index.coerceIn(
                            minimumValue = 0,
                            maximumValue = updatedOrder.size
                        )

                    updatedOrder.add(
                        index = insertIndex,
                        element = indexedEntry.entry
                    )
                }
            }

        return song.copy(
            sections = updatedSections,
            executionOrder = updatedOrder
        )
    }

    private data class IndexedOrderEntry(
        val index: Int,
        val entry: SongOrderEntry
    )
}
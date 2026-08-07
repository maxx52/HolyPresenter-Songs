package org.holypresenter_songs.ui.order

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter.platform.ui.interaction.dragdrop.HolyReorderColumn
import org.holypresenter_songs.domain.editor.command.order.DeleteSongOrderEntryCommand
import org.holypresenter_songs.domain.editor.command.order.MoveSongOrderEntryCommand
import org.holypresenter_songs.presentation.SongEditorContext

@Composable
fun SongOrderPane(
    context: SongEditorContext,
    modifier: Modifier = Modifier
) {
    val song = context.state.song
    val orderEntries = song?.executionOrder.orEmpty()

    val sectionsById =
        song
            ?.sections
            .orEmpty()
            .associateBy { section ->
                section.id
            }

    val selectedOrderEntryId = context.state.selectedOrderEntryId

    Surface(
        modifier = modifier.fillMaxHeight(),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Порядок исполнения",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            HolyReorderColumn(
                items = orderEntries,
                modifier = Modifier.weight(1f),
                onMove = { fromIndex, toIndex ->
                    context.editor.execute(
                        MoveSongOrderEntryCommand(
                            fromIndex = fromIndex,
                            toIndex = toIndex
                        )
                    )
                }
            ) { entry, index, _ ->
                val section = sectionsById[entry.sectionId]

                if (section != null) {
                    SongOrderItem(
                        number = index + 1,
                        section = section,
                        selected = entry.id == selectedOrderEntryId,
                        onClick = {
                            context.state.selectOrderEntry(
                                entry = entry,
                                section = section
                            )
                        },
                        onDelete = {
                            val wasSelected = entry.id == context.state.selectedOrderEntryId

                            /*
                             * После удаления выбираем следующий
                             * элемент, а для последнего —
                             * предыдущий.
                             */
                            val replacementEntryId =
                                if (wasSelected) {
                                    when {
                                        index < orderEntries.lastIndex ->
                                            orderEntries[index + 1].id

                                        index > 0 ->
                                            orderEntries[index - 1].id

                                        else -> null
                                    }
                                } else {
                                    null
                                }

                            context.editor.execute(
                                DeleteSongOrderEntryCommand(
                                    entry = entry
                                )
                            )

                            if (wasSelected) {
                                val updatedSong = context.state.song

                                val replacementEntry =
                                    replacementEntryId?.let { entryId ->
                                        updatedSong
                                            ?.executionOrder
                                            ?.firstOrNull {
                                                it.id == entryId
                                            }
                                        }

                                val replacementSection =
                                    replacementEntry?.let {
                                        updatedEntry ->
                                            updatedSong
                                                ?.sections
                                                ?.firstOrNull {
                                                    section ->
                                                        section.id == updatedEntry.sectionId
                                                }
                                        }

                                if (
                                    replacementEntry != null &&
                                    replacementSection != null
                                ) {
                                    context.state
                                        .selectOrderEntry(
                                            entry = replacementEntry,
                                            section = replacementSection
                                        )
                                } else {
                                    context.state.clearSelection()
                                }
                            }
                        }
                    )
                }
                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
    }
}
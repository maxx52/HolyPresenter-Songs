package org.holypresenter_songs.ui.structure

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSectionType
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.editor.SongEditor
import org.holypresenter_songs.ui.components.AddSectionButton
import org.holypresenter_songs.ui.components.AddSectionDialog
import org.holypresenter_songs.ui.components.SongSectionCard

@Composable
fun SongStructurePane(
    song: Song?,
    modifier: Modifier = Modifier,
    selectedSlide: SongSlide? = null,
    onSlideSelected: (SongSlide) -> Unit = {},
    onAddSection: (SongSection) -> Unit = {},
    onSongChanged: (Song) -> Unit = {},
    editor: SongEditor,
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Структура песни",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(12.dp))

            song?.sections?.forEach { section ->
                SongSectionCard(
                    section = section,
                    selectedSlide = selectedSlide,
                    onSlideSelected = onSlideSelected,
                    onAddSlide = { section ->
                        onSongChanged(editor.addSlide(song, section))
                    },
                    onSlideChanged = { slide, text ->
                        onSongChanged(editor.updateSlideText(song, section, slide, text))
                    },
                    onDeleteSlide = { section, slide ->
                        onSongChanged(editor.deleteSlide(song, section, slide))
                    },
                    onDuplicateSlide = { section, slide ->
                        onSongChanged(editor.duplicateSlide(song, section, slide))
                    },
                    onDeleteSection = { section ->
                        onSongChanged(editor.deleteSection(song, section))
                    },
                    onDuplicateSection = { section ->
                        onSongChanged(editor.duplicateSection(song, section))
                    }
                )
                Spacer(Modifier.height(12.dp))
            }
            AddSectionButton(
                onClick = {
                    showAddDialog = true
                }
            )
        }
    }

    if (showAddDialog) {
        AddSectionDialog(
            onDismiss = {
                showAddDialog = false
            },
            onCreate = { type ->
                onAddSection(
                    SongSection(
                        type = type,
                        number = nextSectionNumber(song, type),
                        slides = listOf(
                            SongSlide(lines = listOf(""))
                        )
                    )
                )
                showAddDialog = false
            }
        )
    }
}

private fun nextSectionNumber(
    song: Song?,
    type: SongSectionType
): Int {
    val maxNumber = song
        ?.sections
        ?.filter { it.type == type }
        ?.maxOfOrNull { it.number }
        ?: 0
    return maxNumber + 1
}
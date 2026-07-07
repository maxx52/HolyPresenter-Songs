package org.holypresenter_songs.ui.structure

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSectionType
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.ui.components.AddSectionButton
import org.holypresenter_songs.ui.components.AddSectionDialog
import org.holypresenter_songs.ui.components.SongSectionItem

@Composable
fun SongStructurePane(
    song: Song?,
    modifier: Modifier = Modifier,
    onAddSection: (SongSection) -> Unit = {},
    onSongChanged: (Song) -> Unit = {},
    selectedSlide: SongSlide? = null,
    onSlideSelected: (SongSlide) -> Unit = {},
    onSlideChanged: (Song) -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Card(modifier = modifier
        .fillMaxHeight()
        .verticalScroll(rememberScrollState())
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Структура песни", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            song?.sections?.forEach { section ->
                SongSectionItem(
                    section = section,
                    selectedSlide = selectedSlide,
                    onSlideSelected = onSlideSelected,
                    onAddSlide = { selectedSection ->
                        val updatedSections = song.sections.map {
                            if (it == selectedSection) {
                                it.copy(
                                    slides = it.slides + SongSlide(
                                        lines = listOf("")
                                    )
                                )
                            } else {
                                it
                            }
                        }
                        val updatedSong = song.copy(
                            sections = updatedSections
                        )
                        onSongChanged(updatedSong)
                    },
                    onSlideChanged = { slide, newText ->
                        val updatedSections = song.sections.map { currentSection ->
                            if (currentSection != section) {
                                currentSection
                            } else {
                                currentSection.copy(
                                    slides = currentSection.slides.map {
                                        if (it == slide) {
                                            it.copy(lines = newText.lines())
                                        } else {
                                            it
                                        }
                                    }
                                )
                            }
                        }
                        val updatedSong = song.copy(
                            sections = updatedSections
                        )
                        onSlideChanged(updatedSong)
                    },
                    onDeleteSlide = { section, slide ->
                        val updatedSections = song.sections.mapNotNull { currentSection ->
                            if (currentSection != section) {
                                currentSection
                            } else {
                                val updatedSlides = currentSection.slides - slide

                                if (updatedSlides.isEmpty()) {
                                    null
                                } else {
                                    currentSection.copy(slides = updatedSlides)
                                }
                            }
                        }
                        val updatedSong = song.copy(sections = updatedSections)
                        onSongChanged(updatedSong)
                    }
                )
                Spacer(Modifier.height(12.dp))
            }
            AddSectionButton(
                onClick = { showAddDialog = true }
            )
        }
    }

    if (showAddDialog) {
        AddSectionDialog(
            onDismiss = { showAddDialog = false },
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
package org.holypresenter_songs.ui.structure

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.domain.SongSectionType
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.editor.command.UpdateSlideChordsCommand
import org.holypresenter_songs.domain.editor.command.UpdateSlideTextCommand
import org.holypresenter_songs.domain.editor.command.section.AddSectionCommand
import org.holypresenter_songs.domain.editor.command.section.DeleteSectionCommand
import org.holypresenter_songs.domain.editor.command.section.DuplicateSectionCommand
import org.holypresenter_songs.domain.editor.command.slide.AddSlideCommand
import org.holypresenter_songs.domain.editor.command.slide.DeleteSlideCommand
import org.holypresenter_songs.domain.editor.command.slide.DuplicateSlideCommand
import org.holypresenter_songs.presentation.SongEditorContext
import org.holypresenter_songs.ui.components.AddSectionButton
import org.holypresenter_songs.ui.components.AddSectionDialog
import org.holypresenter_songs.ui.components.SongSectionCard
import org.holypresenter_songs.domain.editor.command.slide.SplitSlideCommand
import org.holypresenter_songs.domain.editor.command.slide.MergeSlideWithPreviousCommand
import androidx.compose.runtime.key
import org.holypresenter_songs.domain.editor.command.order.AddSongOrderEntryCommand

@Composable
fun SongStructurePane(
    context: SongEditorContext,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val song = context.state.song
    val selectedSlide = context.state.selectedSlide

    Card(
        modifier = modifier.fillMaxHeight()
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
                key(section.id) {
                    SongSectionCard(
                        section = section,
                        selectedSlide = selectedSlide,
                        onSlideSelected = { slide ->
                            context.state.selectSlide(
                                section = section,
                                slide = slide
                            )
                        },
                        onAddToOrder = { selectedSection ->
                            val command = AddSongOrderEntryCommand(
                                    sectionId = selectedSection.id
                                )
                            context.editor.execute(command)

                            val updatedSong = context.state.song

                            val createdEntry =
                                updatedSong
                                    ?.executionOrder
                                    ?.firstOrNull {
                                        it.id == command.entry.id
                                    }

                            val updatedSection =
                                updatedSong
                                    ?.sections
                                    ?.firstOrNull {
                                        it.id == selectedSection.id
                                    }

                            if (
                                createdEntry != null &&
                                updatedSection != null
                            ) {
                                context.state.selectOrderEntry(
                                    entry = createdEntry,
                                    section = updatedSection
                                )
                            }
                        },
                        onAddSlide = { selectedSection ->
                            context.editor.execute(
                                AddSlideCommand(selectedSection)
                            )
                        },
                        onSlideChanged = { slide, text ->
                            context.editor.execute(
                                UpdateSlideTextCommand(
                                    section = section,
                                    slide = slide,
                                    oldText = slide.lines.joinToString("\n"),
                                    newText = text
                                )
                            )
                        },
                        onSlideChordsChanged = { slide, chordsText ->
                            context.editor.execute(
                                UpdateSlideChordsCommand(
                                    section = section,
                                    slide = slide,
                                    newText = chordsText
                                )
                            )
                        },
                        onSlideSplit = { slide, splitOffset ->
                            val command =
                                SplitSlideCommand(
                                    section = section,
                                    slide = slide,
                                    splitOffset = splitOffset
                                )

                            context.editor.execute(command)

                            val updatedSection =
                                context.state.song
                                    ?.sections
                                    ?.firstOrNull {
                                        it.id == section.id
                                    }

                            val createdSlide =
                                updatedSection
                                    ?.slides
                                    ?.firstOrNull {
                                        it.id == command.secondSlide.id
                                    }

                            if (
                                updatedSection != null &&
                                createdSlide != null
                            ) {
                                context.state.selectSlide(
                                    section = updatedSection,
                                    slide = createdSlide
                                )
                            }
                        },
                        onSlideMergeWithPrevious = { slide ->
                            val command =
                                MergeSlideWithPreviousCommand(
                                    section = section,
                                    currentSlide = slide
                                )

                            val mergedSlideId = command.mergedSlideId

                            context.editor.execute(command)

                            val updatedSection =
                                context.state.song
                                    ?.sections
                                    ?.firstOrNull {
                                        it.id == section.id
                                    }

                            val mergedSlide =
                                updatedSection
                                    ?.slides
                                    ?.firstOrNull {
                                        it.id == mergedSlideId
                                    }

                            if (
                                updatedSection != null &&
                                mergedSlide != null
                            ) {
                                context.state.selectSlide(
                                    section = updatedSection,
                                    slide = mergedSlide
                                )
                            }
                        },
                        onDeleteSlide = { selectedSection, slide ->
                            context.editor.execute(
                                DeleteSlideCommand(
                                    section = selectedSection,
                                    slide = slide
                                )
                            )
                        },
                        onDuplicateSlide = { selectedSection, slide ->
                            context.editor.execute(
                                DuplicateSlideCommand(
                                    sourceSection = selectedSection,
                                    sourceSlide = slide
                                )
                            )
                        },
                        onDeleteSection = { selectedSection ->
                            context.editor.execute(
                                DeleteSectionCommand(selectedSection)
                            )
                        },
                        onDuplicateSection = { selectedSection ->
                            context.editor.execute(
                                DuplicateSectionCommand(selectedSection)
                            )
                        }
                    )
                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }
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
                context.editor.execute(
                    AddSectionCommand(
                        SongSection(
                            type = type,
                            number = nextSectionNumber(song, type),
                            slides = listOf(SongSlide(lines = listOf("")))
                        )
                    )
                )
                showAddDialog = false
            }
        )
    }
}

private fun nextSectionNumber(
    song: org.holypresenter_songs.domain.Song?,
    type: SongSectionType
): Int {
    return song
        ?.sections
        ?.filter { it.type == type }
        ?.maxOfOrNull { it.number }
        ?.plus(1)
        ?: 1
}
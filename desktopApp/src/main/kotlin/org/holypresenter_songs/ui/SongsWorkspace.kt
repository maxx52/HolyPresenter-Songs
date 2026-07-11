package org.holypresenter_songs.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import holypresenter.org.platform.api.module.ModuleContext
import holypresenter.org.platform.api.projection.ProjectionService
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.editor.SongEditor
import org.holypresenter_songs.presentation.SongPresentationFactory
import org.holypresenter_songs.presentation.rememberSongEditorState
import org.holypresenter_songs.repository.SongRepository
import org.holypresenter_songs.ui.order.SongOrderPane
import org.holypresenter_songs.ui.preview.SongPreviewPane
import org.holypresenter_songs.ui.structure.SongStructurePane
import org.holypresenter_songs.ui.theme.SongThemePane
import org.holypresenter_songs.ui.topbar.SongsTopBar

@Composable
fun SongsWorkspace(
    context: ModuleContext,
    repository: SongRepository
) {
    val editorState = rememberSongEditorState()
    val songEditor = remember { SongEditor() }
    val presentationFactory = remember { SongPresentationFactory() }

    val projectionService = remember {
        context.services.get(ProjectionService::class)
    }

    LaunchedEffect(Unit) {
        editorState.updateSong(
            repository.getAll().firstOrNull()
        )
    }

    fun applySongChange(updatedSong: Song) {
        repository.save(updatedSong)
        editorState.updateSong(updatedSong)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SongsTopBar(
            song = editorState.song
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SongStructurePane(
                song = editorState.song,
                editor = songEditor,
                modifier = Modifier.weight(0.28f),
                selectedSlide = editorState.selectedSlide,
                onSlideSelected = { slide ->
                    val section = editorState.song
                        ?.sections
                        ?.firstOrNull { slide in it.slides }

                    if (section != null) {
                        editorState.selectSlide(section, slide)
                    }
                },
                onAddSection = { section ->
                    val currentSong = editorState.song ?: return@SongStructurePane
                    applySongChange(
                        songEditor.addSection(currentSong, section)
                    )
                },
                onSongChanged = { updatedSong ->
                    applySongChange(updatedSong)
                }
            )

            SongOrderPane(
                song = editorState.song,
                modifier = Modifier.weight(0.22f),
                onSongChanged = { updatedSong ->
                    applySongChange(updatedSong)
                }
            )

            SongPreviewPane(
                selectedSlide = editorState.selectedSlide,
                modifier = Modifier.weight(0.32f),
                onShowClick = {
                    val currentSong = editorState.song ?: return@SongPreviewPane

                    projectionService?.present(
                        presentationFactory.create(currentSong)
                    )
                }
            )

            SongThemePane(
                modifier = Modifier.weight(0.18f)
            )
        }
    }
}
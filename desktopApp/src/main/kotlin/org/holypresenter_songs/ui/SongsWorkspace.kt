package org.holypresenter_songs.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import holypresenter.org.platform.api.module.ModuleContext
import holypresenter.org.platform.api.projection.ProjectionService
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.editor.SongEditor
import org.holypresenter_songs.presentation.SongPresentationFactory
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
    var song by remember {
        mutableStateOf(repository.getAll().firstOrNull())
    }

    var selectedSlide by remember(song) {
        mutableStateOf(
            song
                ?.sections
                ?.firstOrNull()
                ?.slides
                ?.firstOrNull()
        )
    }

    var themePanelVisible by remember { mutableStateOf(true) }

    val presentationFactory = remember { SongPresentationFactory() }

    val projectionService = remember {
        context.services.get(ProjectionService::class)
    }

    val songEditor = remember { SongEditor() }

    fun applySongChange(updatedSong: Song) {
        repository.save(updatedSong)
        song = updatedSong
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SongsTopBar(song = song)

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SongStructurePane(
                editor = songEditor,
                onSongChanged = { updatedSong ->
                    applySongChange(updatedSong)
                },
                song = song,
                modifier = Modifier.weight(0.28f),
                onAddSection = { section ->
                    song?.let {
                        applySongChange(songEditor.addSection(it, section))
                    }
                },
                selectedSlide = selectedSlide,
                onSlideSelected = {
                    selectedSlide = it
                }
            )

            SongOrderPane(
                song = song,
                modifier = Modifier.weight(0.22f)
            )

            SongPreviewPane(
                selectedSlide = selectedSlide,
                modifier = Modifier.weight(0.32f),
                onShowClick = {
                    val currentSong = song ?: return@SongPreviewPane
                    projectionService?.present(
                        presentationFactory.create(currentSong)
                    )
                }
            )

            if (themePanelVisible) {
                SongThemePane(
                    modifier = Modifier.weight(0.18f)
                )
            }
        }
    }
}
package org.holypresenter_songs.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import holypresenter.org.platform.api.module.ModuleContext
import holypresenter.org.platform.api.projection.ProjectionService
import org.holypresenter_songs.domain.editor.SongEditor
import org.holypresenter_songs.presentation.SongEditorContext
import org.holypresenter_songs.presentation.SongPresentationFactory
import org.holypresenter_songs.presentation.rememberSongEditorState
import org.holypresenter_songs.repository.SongRepository
import org.holypresenter_songs.ui.order.SongOrderPane
import org.holypresenter_songs.ui.presentation.SongPreviewPane
import org.holypresenter_songs.ui.structure.SongStructurePane
import org.holypresenter_songs.ui.theme.SongThemePane
import org.holypresenter_songs.ui.topbar.SongsTopBar

@Composable
fun SongsWorkspace(
    moduleContext: ModuleContext,
    repository: SongRepository
) {
    val editorState = rememberSongEditorState()

    val editor = remember(repository, editorState) {
        SongEditor(
            repository = repository,
            state = editorState
        )
    }

    val editorContext = remember(editorState, editor) {
        SongEditorContext(
            state = editorState,
            editor = editor,
        )
    }

    val presentationFactory = remember {
        SongPresentationFactory()
    }

    val projectionService = remember(moduleContext) {
        moduleContext.services.get(ProjectionService::class)
    }

    LaunchedEffect(repository) {
        editorState.updateSong(
            repository.getAll().firstOrNull()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SongsTopBar(
            context = editorContext
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SongStructurePane(
                context = editorContext,
                modifier = Modifier.weight(0.28f)
            )

            SongOrderPane(
                context = editorContext,
                modifier = Modifier.weight(0.22f),
            )

            SongPreviewPane(
                context = editorContext,
                modifier = Modifier.weight(0.32f),
                onShowClick = {
                    val currentSong = editorContext.state.song ?: return@SongPreviewPane

                    projectionService?.present(
                        presentationFactory.create(currentSong)
                    )
                }
            )

            SongThemePane(
                context = editorContext,
                modifier = Modifier.weight(0.18f)
            )
        }
    }
}
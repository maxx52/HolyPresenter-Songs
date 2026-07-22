package org.holypresenter_songs.ui

import androidx.compose.runtime.Composable
import holypresenter.org.platform.api.module.ModuleContext
import org.holypresenter_songs.domain.factory.SongFactory
import org.holypresenter_songs.presentation.workspace.SongScreen
import org.holypresenter_songs.presentation.workspace.rememberSongWorkspaceState
import org.holypresenter_songs.repository.SongRepository

@Composable
fun SongsWorkspace(
    moduleContext: ModuleContext,
    repository: SongRepository
) {
    val workspaceState = rememberSongWorkspaceState()

    when (workspaceState.screen) {
        SongScreen.LIBRARY -> {
            SongLibraryWorkspace(
                moduleContext = moduleContext,
                repository = repository,
                selectedSongId = workspaceState.selectedSongId,
                onCreateSong = {
                    val newSong = SongFactory.createEmpty()
                    repository.save(newSong)
                    workspaceState.openEditor(newSong.id)
                },
                onOpenSong = { songId ->
                    workspaceState.openEditor(songId)
                }
            )
        }

        SongScreen.EDITOR -> {
            workspaceState.selectedSongId?.let { songId ->
                SongEditorWorkspace(
                    moduleContext = moduleContext,
                    repository = repository,
                    songId = songId,
                    onBackClick = {
                        workspaceState.openLibrary()
                    }
                )
            }
        }
    }
}
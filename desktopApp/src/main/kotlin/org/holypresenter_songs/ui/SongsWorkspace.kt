package org.holypresenter_songs.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import holypresenter.org.platform.api.module.ModuleContext
import holypresenter.org.platform.api.projection.ProjectionContent
import holypresenter.org.platform.api.projection.ProjectionService
import org.holypresenter_songs.domain.factory.SongFactory
import org.holypresenter_songs.presentation.SongPresentationFactory
import org.holypresenter_songs.presentation.workspace.SongScreen
import org.holypresenter_songs.presentation.workspace.SongWorkspaceState
import org.holypresenter_songs.repository.SongRepository
import org.holypresenter_songs.ui.presentation.SongPresenterWorkspace

@Composable
fun SongsWorkspace(
    moduleContext: ModuleContext,
    repository: SongRepository,
    workspaceState: SongWorkspaceState
) {
    val projectionService = remember(moduleContext) {
        moduleContext.services.get(
            ProjectionService::class
        )
    }

    val presentationFactory = remember {
        SongPresentationFactory()
    }

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

        SongScreen.PRESENTER -> {
            workspaceState.selectedSongId?.let { songId ->
                SongPresenterWorkspace(
                    moduleContext = moduleContext,
                    repository = repository,
                    songId = songId,
                    onBackClick = {
                        workspaceState.openLibrary()
                    },
                    onEditClick = {
                        workspaceState.openEditor(songId)
                    },
                    onSlideClick = { song, _, globalIndex ->
                        val presentation = presentationFactory.create(song)

                        projectionService?.show(
                            ProjectionContent.Slide(
                                presentation = presentation,
                                slideIndex = globalIndex
                            )
                        )
                    }
                )
            }
        }
    }
}
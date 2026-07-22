package org.holypresenter_songs.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import holypresenter.org.platform.api.module.ModuleContext
import holypresenter.org.platform.api.planner.PlannerService
import org.holypresenter.platform.ui.workspace.HolyWorkspace
import org.holypresenter_songs.domain.SongId
import org.holypresenter_songs.repository.SongRepository
import org.holypresenter_songs.ui.library.SongLibraryPane

@Composable
fun SongLibraryWorkspace(
    moduleContext: ModuleContext,
    repository: SongRepository,
    selectedSongId: SongId?,
    onCreateSong: () -> Unit,
    onOpenSong: (SongId) -> Unit
) {
    val plannerService = remember(moduleContext) {
        moduleContext.services.get(
            PlannerService::class
        )
    }

    HolyWorkspace(
        left = {
            SongLibraryPane(
                repository = repository,
                selectedSongId = selectedSongId,
                plannerService = plannerService,
                onCreateSong = onCreateSong,
                onOpenSong = onOpenSong
            )
        },

        right = {
            PlannerSidePane(
                plannerService = plannerService
            )
        }
    )
}
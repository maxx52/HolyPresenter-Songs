package org.holypresenter_songs.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import holypresenter.org.platform.api.module.ModuleContext
import holypresenter.org.platform.api.planner.PlannerItemHandlerRegistry
import holypresenter.org.platform.api.planner.PlannerService
import org.holypresenter.platform.ui.planner.PlannerSidePane
import org.holypresenter.platform.ui.workspace.HolyWorkspace
import org.holypresenter_songs.domain.SongId
import org.holypresenter_songs.importer.DefaultSongTextParser
import org.holypresenter_songs.importer.SongImportDialog
import org.holypresenter_songs.importer.SongImportDraft
import org.holypresenter_songs.repository.SongRepository
import org.holypresenter_songs.ui.library.SongLibraryPane

@Composable
fun SongLibraryWorkspace(
    moduleContext: ModuleContext,
    repository: SongRepository,
    selectedSongId: SongId?,
    onCreateSong: () -> Unit,
    onImportSong: (SongImportDraft) -> Unit,
    onOpenSong: (SongId) -> Unit
) {
    var isImportDialogOpen by remember {
        mutableStateOf(false)
    }

    val songTextParser = remember {
        DefaultSongTextParser()
    }

    val plannerService = remember(moduleContext) {
        moduleContext.services.get(
            PlannerService::class
        )
    }

    val plannerItemHandlerRegistry =
        remember(moduleContext) {
            moduleContext.services.get(
                PlannerItemHandlerRegistry::class
            )
        }

    HolyWorkspace(
        left = {
            SongLibraryPane(
                repository = repository,
                selectedSongId = selectedSongId,
                plannerService = plannerService,
                onCreateSong = onCreateSong,
                onImportSong = {
                    isImportDialogOpen = true
                },
                onOpenSong = onOpenSong
            )
        },
        right = {
            PlannerSidePane(
                plannerService = plannerService,
                onItemClick = { item, _ ->
                    plannerItemHandlerRegistry?.activate(item)
                }
            )
        }
    )

    if (isImportDialogOpen) {
        SongImportDialog(
            parser = songTextParser,
            onDismissRequest = {
                isImportDialogOpen = false
            },
            onImport = { draft ->
                isImportDialogOpen = false
                onImportSong(draft)
            }
        )
    }
}
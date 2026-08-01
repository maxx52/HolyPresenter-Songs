package org.holypresenter_songs.ui.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import holypresenter.org.platform.api.module.ModuleContext
import holypresenter.org.platform.api.planner.PlannerItem
import holypresenter.org.platform.api.planner.PlannerItemHandlerRegistry
import holypresenter.org.platform.api.planner.PlannerService
import org.holypresenter.platform.ui.workspace.HolyWorkspace
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongId
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.repository.SongRepository
import org.holypresenter.platform.ui.planner.PlannerSidePane

@Composable
fun SongPresenterWorkspace(
    moduleContext: ModuleContext,
    repository: SongRepository,
    songId: SongId,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onSlideClick: (song: Song, slide: SongSlide, globalIndex: Int) -> Unit
) {
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

    val song = remember(songId) {
        repository.findById(songId)
    }

    HolyWorkspace(
        modifier = Modifier.fillMaxSize(),
        left = {
            if (song != null) {
                SongSlidesPane(
                    moduleContext = moduleContext,
                    song = song,
                    onBackClick = onBackClick,
                    onEditClick = onEditClick,
                    onSlideClick = { slide, globalIndex ->
                        onSlideClick(
                            song,
                            slide,
                            globalIndex
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Песня не найдена")
                }
            }
        },

        right = {
            PlannerSidePane(
                plannerService = plannerService,
                modifier = Modifier.fillMaxSize(),
                onItemClick = { item, index ->
                    val currentActiveIndex = plannerService?.state?.activeItemIndex

                    if (currentActiveIndex == index) {
                        plannerService.clearActive()
                    } else {
                        val activated = plannerItemHandlerRegistry?.activate(item) == true

                        if (activated) {
                            plannerService?.setActive(index)
                        }
                    }
                }
            )
        }
    )
}
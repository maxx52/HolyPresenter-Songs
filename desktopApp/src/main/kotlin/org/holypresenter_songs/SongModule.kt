package org.holypresenter_songs

import androidx.compose.runtime.Composable
import holypresenter.org.platform.api.module.HolyModule
import holypresenter.org.platform.api.module.ModuleContext
import holypresenter.org.platform.api.module.ModuleMetadata
import holypresenter.org.platform.api.planner.PlannerItemHandlerRegistry
import org.holypresenter_songs.planner.SongPlannerItemHandler
import org.holypresenter_songs.presentation.workspace.SongWorkspaceState
import org.holypresenter_songs.repository.JsonSongRepository
import org.holypresenter_songs.ui.SongsWorkspace
import java.io.File

class SongModule : HolyModule {
    private val repository =
        JsonSongRepository(
            songsDirectory = File(
                "HolyPresenter",
                "songs"
            )
        )

    /**
     * Единое состояние рабочего пространства
     * на всё время жизни модуля.
     */
    private val workspaceState = SongWorkspaceState()

    private lateinit var context: ModuleContext
    private var plannerItemHandlerRegistry: PlannerItemHandlerRegistry? = null

    override fun onLoad(
        context: ModuleContext
    ) {
        this.context = context
    }

    override fun onEnable(
        context: ModuleContext
    ) {
        val registry =
            context.services.get(
                PlannerItemHandlerRegistry::class
            )

        plannerItemHandlerRegistry = registry

        registry?.register(
            SongPlannerItemHandler(
                repository = repository,
                onActivateSong = workspaceState::openPresenter
            )
        )
    }

    override fun onDisable() {
        plannerItemHandlerRegistry?.unregister(metadata.id)
        plannerItemHandlerRegistry = null
    }

    @Composable
    override fun Workspace() {
        SongsWorkspace(
            moduleContext = context,
            repository = repository,
            workspaceState = workspaceState
        )
    }

    override val metadata =
        ModuleMetadata(
            id = "songs",
            name = "Songs",
            version = "1.0.0",
            apiVersion = "0.6.0",
            author = "HolyPresenter",
            description = "Song management module"
        )
}
package org.holypresenter_songs

import androidx.compose.runtime.Composable
import holypresenter.org.platform.api.module.HolyModule
import holypresenter.org.platform.api.module.ModuleContext
import holypresenter.org.platform.api.module.ModuleMetadata
import org.holypresenter_songs.repository.InMemorySongRepository
import org.holypresenter_songs.repository.SongRepository
import org.holypresenter_songs.ui.SongsWorkspace

class SongsModule : HolyModule {
    private val repository: SongRepository = InMemorySongRepository()
    private lateinit var context: ModuleContext

    override fun onLoad(context: ModuleContext) {
        this.context = context
    }

    override val metadata = ModuleMetadata(
        id = "songs",
        name = "Songs",
        version = "1.0.0",
        apiVersion = "0.1.0",
        author = "HolyPresenter",
        description = "Song management module"
    )

    @Composable
    override fun Workspace() {
        SongsWorkspace(
            context = context,
            repository = repository
        )
    }
}
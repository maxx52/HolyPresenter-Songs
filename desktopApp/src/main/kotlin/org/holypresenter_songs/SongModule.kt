package org.holypresenter_songs

import androidx.compose.runtime.Composable
import holypresenter.org.platform.api.module.HolyModule
import holypresenter.org.platform.api.module.ModuleContext
import holypresenter.org.platform.api.module.ModuleMetadata
import org.holypresenter_songs.repository.JsonSongRepository
import org.holypresenter_songs.ui.SongsWorkspace
import java.io.File

class SongModule : HolyModule {
    private val repository = JsonSongRepository(
        songsDirectory = File(
            "HolyPresenter",
            "songs"
        )
    )
    private lateinit var context: ModuleContext

    override fun onLoad(context: ModuleContext) {
        this.context = context
    }

    @Composable
    override fun Workspace() {
        SongsWorkspace(
            moduleContext = context,
            repository = repository
        )
    }

    override val metadata = ModuleMetadata(
        id = "songs",
        name = "Songs",
        version = "1.0.0",
        apiVersion = "0.1.0",
        author = "HolyPresenter",
        description = "Song management module"
    )
}
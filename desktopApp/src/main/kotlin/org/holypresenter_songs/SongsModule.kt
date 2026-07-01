package org.holypresenter_songs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import holypresenter.org.platform.api.module.HolyModule
import holypresenter.org.platform.api.module.ModuleMetadata

class SongsModule : HolyModule {
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
        Text("Songs Module")
    }
}
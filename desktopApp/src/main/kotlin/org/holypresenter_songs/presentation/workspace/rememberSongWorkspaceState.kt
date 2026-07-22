package org.holypresenter_songs.presentation.workspace

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberSongWorkspaceState(): SongWorkspaceState =
    remember {
        SongWorkspaceState()
    }
package org.holypresenter_songs.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberSongEditorState() =
    remember {
        SongEditorState()
    }
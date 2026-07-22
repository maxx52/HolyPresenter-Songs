package org.holypresenter_songs.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.holypresenter_songs.domain.Song

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SongListItem(
    song: Song,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(song.metadata.title)
        },
        modifier = Modifier.clickable(
            onClick = onClick
        )
    )
}
package org.holypresenter_songs.ui

import androidx.compose.runtime.Composable
import org.holypresenter_songs.domain.Song

@Composable
fun SongCard(
    song: Song,
    onShow: (Song) -> Unit
) {}
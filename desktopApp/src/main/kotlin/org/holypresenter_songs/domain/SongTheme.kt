package org.holypresenter_songs.domain

import kotlinx.serialization.Serializable

@Serializable
data class SongTheme(
    val background: SongBackground = SongBackground.None,
    val textStyle: SongTextStyle = SongTextStyle(),
    val overlay: SongOverlay = SongOverlay()
)
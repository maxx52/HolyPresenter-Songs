package org.holypresenter_songs.domain

data class SongTheme(
    val background: SongBackground = SongBackground.None,
    val textStyle: SongTextStyle = SongTextStyle(),
    val overlay: SongOverlay = SongOverlay()
)
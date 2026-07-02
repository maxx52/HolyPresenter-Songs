package org.holypresenter_songs.domain

data class SongTextStyle(
    val fontFamily: String? = null,
    val fontSize: Int = 64,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val outlineEnabled: Boolean = true,
    val shadowEnabled: Boolean = true
)
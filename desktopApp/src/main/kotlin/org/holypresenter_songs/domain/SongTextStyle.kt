package org.holypresenter_songs.domain

import kotlinx.serialization.Serializable

@Serializable
data class SongTextStyle(
    val fontFamily: String? = null,
    val fontSize: Int = 64,
    val textColor: Long = 0xFFFFFFFF,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val outlineEnabled: Boolean = true,
    val shadowEnabled: Boolean = true
)
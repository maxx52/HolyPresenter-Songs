package org.holypresenter_songs.domain

import kotlinx.serialization.Serializable

@Serializable
data class SongOverlay(
    val enabled: Boolean = true,
    val opacity: Float = 0.45f
)
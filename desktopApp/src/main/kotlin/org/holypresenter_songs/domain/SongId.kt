package org.holypresenter_songs.domain

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@JvmInline
value class SongId(
    val value: String
) {
    companion object {
        fun random() = SongId(
            UUID.randomUUID().toString()
        )
    }
}
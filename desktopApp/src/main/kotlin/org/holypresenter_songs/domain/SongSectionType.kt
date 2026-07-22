package org.holypresenter_songs.domain

import kotlinx.serialization.Serializable

@Serializable
enum class SongSectionType {
    VERSE,
    CHORUS,
    PRE_CHORUS,
    BRIDGE,
    TAG,
    INTRO,
    ENDING
}
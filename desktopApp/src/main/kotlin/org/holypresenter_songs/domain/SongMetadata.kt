package org.holypresenter_songs.domain

data class SongMetadata(
    val title: String,
    val author: String? = null,
    val copyright: String? = null,
    val ccli: String? = null,
    val language: String? = null,
    val tags: Set<String> = emptySet()
)
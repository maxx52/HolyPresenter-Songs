package org.holypresenter_songs.domain

data class SongSection(
    val type: SongSectionType,
    val number: Int = 1,
    val slides: List<SongSlide>
)
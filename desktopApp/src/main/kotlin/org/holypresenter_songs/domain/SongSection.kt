package org.holypresenter_songs.domain

import holypresenter.org.platform.api.model.HolyId
import holypresenter.org.platform.api.model.HolyIds

data class SongSection(
    val id: HolyId = HolyIds.newId(),
    val type: SongSectionType,
    val number: Int,
    val slides: List<SongSlide>
)
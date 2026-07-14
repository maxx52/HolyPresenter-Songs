package org.holypresenter_songs.domain

import holypresenter.org.platform.api.model.HolyId
import holypresenter.org.platform.api.model.HolyIds

data class SongSlide(
    val id: HolyId = HolyIds.newId(),
    val lines: List<String>
)
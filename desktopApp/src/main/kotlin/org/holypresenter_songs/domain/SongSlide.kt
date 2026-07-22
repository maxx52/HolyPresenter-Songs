package org.holypresenter_songs.domain

import holypresenter.org.platform.api.model.HolyId
import holypresenter.org.platform.api.model.HolyIds
import kotlinx.serialization.Serializable

@Serializable
data class SongSlide(
    val id: HolyId = HolyIds.newId(),
    val lines: List<String>
)
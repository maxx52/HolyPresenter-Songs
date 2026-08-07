package org.holypresenter_songs.domain

import holypresenter.org.platform.api.model.HolyId
import holypresenter.org.platform.api.model.HolyIds
import kotlinx.serialization.Serializable

/**
 * Одно вхождение секции в порядок исполнения.
 *
 * Одна и та же секция может присутствовать
 * в порядке несколько раз. Каждое вхождение
 * при этом имеет собственный уникальный id.
 */
@Serializable
data class SongOrderEntry(
    val id: HolyId = HolyIds.newId(),
    val sectionId: HolyId
)
package org.holypresenter_songs.planner

import holypresenter.org.platform.api.planner.PlannerItem
import holypresenter.org.platform.api.planner.PlannerItemHandler
import org.holypresenter_songs.domain.SongId
import org.holypresenter_songs.repository.SongRepository

internal class SongPlannerItemHandler(
    private val repository: SongRepository,
    private val onActivateSong: (SongId) -> Unit
) : PlannerItemHandler {
    override val moduleId: String = "songs"

    override fun activate(
        item: PlannerItem
    ): Boolean {
        if (item.reference.moduleId != moduleId) {
            return false
        }

        val songId = SongId(item.reference.itemId)

        val songExists = repository.findById(songId) != null

        if (!songExists) {
            return false
        }
        onActivateSong(songId)
        return true
    }
}
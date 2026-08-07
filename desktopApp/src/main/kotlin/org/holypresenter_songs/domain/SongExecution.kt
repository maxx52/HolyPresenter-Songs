package org.holypresenter_songs.domain

data class SongExecutionSection(
    val entry: SongOrderEntry,
    val section: SongSection
)

fun Song.executionSections(): List<SongExecutionSection> {
    val sectionsById =
        sections.associateBy { section ->
            section.id
        }

    return executionOrder.mapNotNull { entry ->
        val section =
            sectionsById[entry.sectionId]
                ?: return@mapNotNull null

        SongExecutionSection(
            entry = entry,
            section = section
        )
    }
}

fun Song.executionSlides(): List<SongSlide> =
    executionSections()
        .flatMap { executionSection ->
            executionSection.section.slides
        }
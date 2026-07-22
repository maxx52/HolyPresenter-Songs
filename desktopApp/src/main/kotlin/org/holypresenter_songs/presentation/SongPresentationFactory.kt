package org.holypresenter_songs.presentation

import holypresenter.org.platform.api.presentation.Presentation
import holypresenter.org.platform.api.presentation.PresentationSlide
import holypresenter.org.platform.api.presentation.SlotId
import holypresenter.org.platform.api.presentation.element.TextElement
import org.holypresenter_songs.domain.Song

class SongPresentationFactory {
    fun create(song: Song): Presentation {
        return Presentation(
            id = song.id.value,
            slides = song.sections
                .flatMap { it.slides }
                .mapIndexed { index, slide ->
                PresentationSlide(
                    id = "${song.id.value}-slide-$index",
                    elements = listOf(
                        TextElement(
                            id = "${song.id.value}-text-$index",
                            slot = SlotId("lyrics"),
                            text = slide.lines.joinToString("\n")
                        )
                    )
                )
            }
        )
    }
}

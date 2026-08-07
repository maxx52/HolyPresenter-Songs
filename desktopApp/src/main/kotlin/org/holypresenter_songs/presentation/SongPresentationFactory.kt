package org.holypresenter_songs.presentation

import holypresenter.org.platform.api.presentation.Presentation
import holypresenter.org.platform.api.presentation.PresentationMetadata
import holypresenter.org.platform.api.presentation.PresentationSlide
import holypresenter.org.platform.api.presentation.SlotId
import holypresenter.org.platform.api.presentation.element.TextElement
import holypresenter.org.platform.api.presentation.theme.PresentationBackground
import holypresenter.org.platform.api.presentation.theme.PresentationBackgroundType
import holypresenter.org.platform.api.presentation.theme.PresentationOverlay
import holypresenter.org.platform.api.presentation.theme.PresentationTextStyle
import holypresenter.org.platform.api.presentation.theme.PresentationTheme
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongBackground
import org.holypresenter_songs.domain.executionSlides

class SongPresentationFactory {
    fun create(song: Song): Presentation =
        Presentation(
            id = song.id.value,
            metadata = PresentationMetadata(
                title = song.metadata.title,
                author = song.metadata.author,
                copyright = song.metadata.copyright,
                language = song.metadata.language,
                tags = song.metadata.tags.toList()
            ),
            theme = song.toPresentationTheme(),
            slides = song
                .executionSlides()
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

private fun Song.toPresentationTheme(): PresentationTheme =
    PresentationTheme(
        background = when (
            val songBackground = theme.background
        ) {
            SongBackground.None ->
                PresentationBackground(
                    type = PresentationBackgroundType.COLOR,
                    color = 0xFF000000
                )

            is SongBackground.Image ->
                PresentationBackground(
                    type = PresentationBackgroundType.IMAGE,
                    path = songBackground.path
                )

            is SongBackground.Video ->
                PresentationBackground(
                    type = PresentationBackgroundType.VIDEO,
                    path = songBackground.path
                )
        },
        textStyle = PresentationTextStyle(
            fontFamily = theme.textStyle.fontFamily,
            fontSize = theme.textStyle.fontSize,
            textColor = theme.textStyle.textColor,
            bold = theme.textStyle.bold,
            italic = theme.textStyle.italic,
            outlineEnabled = theme.textStyle.outlineEnabled,
            shadowEnabled = theme.textStyle.shadowEnabled
        ),
        overlay = PresentationOverlay(
            enabled = theme.overlay.enabled,
            opacity = theme.overlay.opacity
        )
    )
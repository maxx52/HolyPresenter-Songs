package org.holypresenter_songs.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import holypresenter.org.platform.api.module.ModuleContext
import holypresenter.org.platform.api.projection.ProjectionService
import holypresenter.org.platform.api.video.VideoOverlayContent
import holypresenter.org.platform.api.video.VideoPlaybackStatus
import org.holypresenter_songs.domain.SongBackground
import org.holypresenter_songs.domain.SongId
import org.holypresenter_songs.domain.Song
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.editor.SongEditor
import org.holypresenter_songs.presentation.SongEditorContext
import org.holypresenter_songs.presentation.SongPresentationFactory
import org.holypresenter_songs.presentation.rememberSongEditorState
import org.holypresenter_songs.repository.SongRepository
import org.holypresenter_songs.ui.order.SongOrderPane
import org.holypresenter_songs.ui.presentation.SongPreviewPane
import org.holypresenter_songs.ui.structure.SongStructurePane
import org.holypresenter_songs.ui.theme.SongThemePane
import org.holypresenter_songs.ui.topbar.SongsTopBar
import holypresenter.org.platform.api.video.VideoPlaybackService

@Composable
fun SongEditorWorkspace(
    moduleContext: ModuleContext,
    repository: SongRepository,
    songId: SongId,
    onBackClick: () -> Unit
) {
    val editorState = rememberSongEditorState()

    val editor = remember(repository, editorState) {
        SongEditor(
            repository = repository,
            state = editorState
        )
    }

    val editorContext = remember(editorState, editor) {
        SongEditorContext(
            state = editorState,
            editor = editor,
        )
    }

    val presentationFactory = remember {
        SongPresentationFactory()
    }

    val projectionService = remember(moduleContext) {
        moduleContext.services.get(ProjectionService::class)
    }

    val videoPlaybackService = remember(moduleContext) {
        moduleContext.services.get(
            VideoPlaybackService::class
        )
    }

    LaunchedEffect(songId) {
        editorState.updateSong(
            repository.findById(songId)
        )
    }

    LaunchedEffect(editorState.song, editorState.selectedSlide) {
        val currentSong = editorState.song
        val selectedSlide = editorState.selectedSlide

        if (
            currentSong != null &&
            selectedSlide != null &&
            videoPlaybackService?.state?.status == VideoPlaybackStatus.PLAYING
        ) {
            videoPlaybackService.updateOverlay(
                currentSong.videoOverlayContent(selectedSlide)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SongsTopBar(
            context = editorContext,
            onBackClick = onBackClick
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SongStructurePane(
                context = editorContext,
                modifier = Modifier.weight(0.28f)
            )

            SongOrderPane(
                context = editorContext,
                modifier = Modifier.weight(0.22f),
            )

            SongPreviewPane(
                context = editorContext,
                modifier = Modifier.weight(0.32f),
                onShowClick = {
                    val currentSong = editorState.song ?: return@SongPreviewPane
                    val selectedSlide = editorState.selectedSlide
                        ?: return@SongPreviewPane

                    when (val background = currentSong.theme.background) {
                        is SongBackground.Video -> {
                            videoPlaybackService?.updateOverlay(
                                currentSong.videoOverlayContent(selectedSlide)
                            )
                            videoPlaybackService?.play(
                                path = background.path,
                                loop = true,
                                muted = true
                            )
                        }
                        else -> {
                            videoPlaybackService?.stop()
                        }
                    }

                    val presentation = presentationFactory.create(currentSong)
                    projectionService?.present(presentation)
                    currentSong.slideIndex(selectedSlide)?.let { index ->
                        projectionService?.goTo(index)
                    }
                }
            )

            SongThemePane(
                context = editorContext,
                modifier = Modifier.weight(0.18f)
            )
        }
    }
}

private fun Song.videoOverlayContent(
    slide: SongSlide
): VideoOverlayContent =
    VideoOverlayContent(
        text = slide.lines.joinToString("\n"),
        overlayOpacity = if (theme.overlay.enabled) theme.overlay.opacity else 0f,
        textColor = theme.textStyle.textColor,
        fontFamily = theme.textStyle.fontFamily,
        fontSize = theme.textStyle.fontSize,
        bold = theme.textStyle.bold,
        italic = theme.textStyle.italic,
        outlineEnabled = theme.textStyle.outlineEnabled,
        shadowEnabled = theme.textStyle.shadowEnabled
    )

private fun Song.slideIndex(slide: SongSlide): Int? =
    sections
        .flatMap { it.slides }
        .indexOf(slide)
        .takeIf { it >= 0 }

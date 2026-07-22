package org.holypresenter_songs.ui.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.SongTheme
import org.holypresenter_songs.presentation.SongEditorContext

@Composable
fun SongPreviewPane(
    context: SongEditorContext,
    modifier: Modifier = Modifier,
    onShowClick: () -> Unit
) {
    val slide = context.state.selectedSlide
    val song = context.state.song
    val theme = song?.theme ?: SongTheme()

    val previewTheme = theme.copy(
        overlay = theme.overlay.copy(
            opacity = context.state.previewOverlayOpacity
                ?: theme.overlay.opacity
        ),
        textStyle = theme.textStyle.copy(
            fontSize = context.state.previewFontSize
                ?: theme.textStyle.fontSize
        )
    )

    Card(
        modifier = modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Предпросмотр",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(12.dp))

            SongPresentationSurface(
                theme = previewTheme
            ) {
                SongTextLayer(
                    slide = slide,
                    theme = previewTheme
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onShowClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Показать на проекторе")
            }
        }
    }
}
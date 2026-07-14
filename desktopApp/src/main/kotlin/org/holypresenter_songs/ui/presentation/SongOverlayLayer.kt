package org.holypresenter_songs.ui.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.holypresenter_songs.domain.SongTheme

@Composable
fun BoxScope.SongOverlayLayer(
    theme: SongTheme
) {
    val overlay = theme.overlay

    if (!overlay.enabled) return

    Box(
        modifier = Modifier
            .matchParentSize()
            .background(
                Color.Black.copy(
                    alpha = overlay.opacity.coerceIn(0f, 1f)
                )
            )
    )
}
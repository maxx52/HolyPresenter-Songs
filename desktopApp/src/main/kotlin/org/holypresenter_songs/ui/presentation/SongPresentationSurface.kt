package org.holypresenter_songs.ui.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.SongTheme
import org.holypresenter_songs.ui.presentation.background.SongBackgroundLayer

@Composable
fun SongPresentationSurface(
    theme: SongTheme,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(shape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = shape
            )
    ) {
        // Порядок слоёв важен
        SongBackgroundLayer(theme)
        SongOverlayLayer(theme)
        content()
    }
}
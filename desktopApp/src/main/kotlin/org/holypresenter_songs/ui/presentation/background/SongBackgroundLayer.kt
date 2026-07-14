package org.holypresenter_songs.ui.presentation.background

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.holypresenter_songs.domain.SongBackground
import org.holypresenter_songs.domain.SongTheme

@Composable
fun BoxScope.SongBackgroundLayer(
    theme: SongTheme
) {
    when (val background = theme.background) {
        SongBackground.None -> {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xFF7C3AED))
            )
        }

        is SongBackground.Image -> {
            SongBackgroundImageLayer(
                path = background.path
            )
        }

        is SongBackground.Video -> {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black)
            )
        }
    }
}
package org.holypresenter_songs.ui.presentation

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.SongTheme

private const val REFERENCE_PROJECTOR_WIDTH = 1920f

@Composable
fun BoxScope.SongTextLayer(
    slide: SongSlide?,
    theme: SongTheme
) {
    val style = theme.textStyle

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        val previewScale = (maxWidth.value / REFERENCE_PROJECTOR_WIDTH)
                .coerceAtLeast(0.01f)
        val scaledFontSize = (style.fontSize * previewScale).sp
        val scaledLineHeight = (style.fontSize * 1.15f * previewScale).sp

        val fontFamily = when (style.fontFamily) {
            "Arial" -> FontFamily.SansSerif
            "Times New Roman" -> FontFamily.Serif
            else -> FontFamily.SansSerif
        }

        Text(
            text = slide
                ?.lines
                ?.joinToString("\n")
                .orEmpty(),
            color = Color.White,
            fontSize = scaledFontSize,
            lineHeight = scaledLineHeight,
            fontWeight = if (style.bold) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            },
            fontStyle = if (style.italic) {
                FontStyle.Italic
            } else {
                FontStyle.Normal
            },
            textAlign = TextAlign.Center,
            softWrap = true,
            fontFamily = fontFamily,
        )
    }
}
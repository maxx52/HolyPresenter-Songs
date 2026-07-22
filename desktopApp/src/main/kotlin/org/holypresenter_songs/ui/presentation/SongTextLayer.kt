package org.holypresenter_songs.ui.presentation

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.holypresenter_songs.domain.SongSlide
import org.holypresenter_songs.domain.SongTextStyle
import org.holypresenter_songs.domain.SongTheme

private const val REFERENCE_PROJECTOR_WIDTH = 1920f

@Composable
fun BoxScope.SongTextLayer(
    slide: SongSlide?,
    theme: SongTheme
) {
    val style = theme.textStyle
    val text = slide?.lines?.joinToString("\n").orEmpty()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        val previewScale =
            (maxWidth.value / REFERENCE_PROJECTOR_WIDTH)
                .coerceAtLeast(0.01f)

        val scaledFontSize =
            (style.fontSize * previewScale).sp

        val scaledLineHeight =
            (style.fontSize * 1.15f * previewScale).sp

        val fontFamily = when (style.fontFamily) {
            "Times New Roman" -> FontFamily.Serif
            else -> FontFamily.SansSerif
        }

        val textColor = Color(style.textColor)
        val outlineColor = Color.Black

        if (style.outlineEnabled) {
            val width = outlineWidth(
                style = style,
                previewScale = previewScale
            )

            outlineOffsets(width).forEach { offset ->
                TextLayer(
                    text = text,
                    modifier = Modifier.offset(
                        x = offset.x,
                        y = offset.y
                    ),
                    color = outlineColor,
                    fontFamily = fontFamily,
                    fontSize = scaledFontSize,
                    lineHeight = scaledLineHeight,
                    style = style
                )
            }
        }

        TextLayer(
            text = text,
            modifier = Modifier,
            color = textColor,
            fontFamily = fontFamily,
            fontSize = scaledFontSize,
            lineHeight = scaledLineHeight,
            style = style
        )
    }
}

private fun outlineWidth(
    style: SongTextStyle,
    previewScale: Float
): Dp = ((style.fontSize / 40f).coerceIn(1f, 4f) * previewScale)
        .coerceAtLeast(0.5f)
        .dp

private fun outlineOffsets(
    width: Dp
): List<DpOffset> =
    listOf(
        DpOffset(-width, -width),
        DpOffset(0.dp, -width),
        DpOffset(width, -width),

        DpOffset(-width, 0.dp),
        DpOffset(width, 0.dp),

        DpOffset(-width, width),
        DpOffset(0.dp, width),
        DpOffset(width, width)
    )

@Composable
private fun TextLayer(
    text: String,
    modifier: Modifier,
    color: Color,
    fontFamily: FontFamily,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    style: SongTextStyle
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontFamily = fontFamily,
        fontSize = fontSize,
        lineHeight = lineHeight,
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
        softWrap = true
    )
}
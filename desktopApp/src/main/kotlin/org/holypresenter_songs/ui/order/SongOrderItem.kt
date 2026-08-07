package org.holypresenter_songs.ui.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.ui.common.accentColor
import org.holypresenter_songs.ui.common.displayTitle
import org.holypresenter_songs.ui.common.shortTitle

@Composable
fun SongOrderItem(
    number: Int,
    section: SongSection,
    selected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = section.type.accentColor()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier =
                Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) {
                            accentColor
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.bodySmall,
                color =
                    if (selected) {
                        Color.White
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Surface(
            modifier =
                Modifier
                    .weight(1f)
                    .clickable(onClick = onClick),
            shape = RoundedCornerShape(14.dp),
            color =
                accentColor.copy(
                    alpha =
                        if (selected) {
                            0.22f
                        } else {
                            0.12f
                        }
                ),
            tonalElevation =
                if (selected) {
                    3.dp
                } else {
                    1.dp
                },
            border =
                if (selected) {
                    BorderStroke(
                        width = 2.dp,
                        color = accentColor
                    )
                } else {
                    null
                }
        ) {
            Row(
                modifier =
                    Modifier.padding(
                        start = 14.dp,
                        end = 6.dp,
                        top = 10.dp,
                        bottom = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = section.type.shortTitle(),
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = section.displayTitle(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = lineCountText(
                            section.slides.sumOf { slide ->
                                slide.lines.size
                            }
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {
                    Text(
                        text = "🗑",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

private fun lineCountText(
    count: Int
): String {
    val lastTwoDigits = count % 100
    val lastDigit = count % 10

    val word =
        when {
            lastTwoDigits in 11..14 -> "строк"
            lastDigit == 1 -> "строка"
            lastDigit in 2..4 -> "строки"
            else -> "строк"
        }

    return "$count $word"
}
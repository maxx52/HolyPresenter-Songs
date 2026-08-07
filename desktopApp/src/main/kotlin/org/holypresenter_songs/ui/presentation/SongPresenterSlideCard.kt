package org.holypresenter_songs.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.SongSlide

@Composable
fun SongPresenterSlideCard(
    slide: SongSlide,
    number: Int,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border =
            if (selected) {
                BorderStroke(
                    width = 2.dp,
                    color = accentColor
                )
            } else {
                null
            },
        colors =
            CardDefaults.cardColors(
                containerColor =
                    accentColor.copy(
                        alpha =
                            if (selected) {
                                0.22f
                            } else {
                                0.08f
                            }
                    ),
                contentColor = MaterialTheme.colorScheme.onSurface
            )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = "Слайд $number",
                style = MaterialTheme.typography.labelMedium,
                color = accentColor
            )

            Text(
                text =
                    slide.lines
                        .joinToString("\n")
                        .ifBlank {
                            "Пустой слайд"
                        },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
package org.holypresenter_songs.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.holypresenter_songs.domain.SongSectionType

@Composable
fun SongOrderItem(
    number: Int,
    section: SongSection,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.width(10.dp))

        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = sectionColor(section.type).copy(alpha = 0.12f),
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(sectionColor(section.type)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sectionShortName(section.type),
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column {
                    Text(
                        text = sectionTitle(section),
                        style = MaterialTheme.typography.titleSmall
                    )

                    Text(
                        text = "${section.slides.sumOf { it.lines.size }} строки",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(Modifier.weight(1f))

                Text("🗑")
            }
        }
    }
}

private fun sectionTitle(section: SongSection): String =
    when (section.type) {
        SongSectionType.VERSE -> "Куплет ${section.number}"
        SongSectionType.CHORUS -> "Припев"
        SongSectionType.PRE_CHORUS -> "Предприпев"
        SongSectionType.BRIDGE -> "Бридж"
        SongSectionType.INTRO -> "Интро"
        SongSectionType.ENDING -> "Финал"
        SongSectionType.TAG -> "Тег"
    }

private fun sectionShortName(type: SongSectionType): String =
    when (type) {
        SongSectionType.VERSE -> "К"
        SongSectionType.CHORUS -> "П"
        SongSectionType.PRE_CHORUS -> "ПП"
        SongSectionType.BRIDGE -> "Б"
        SongSectionType.INTRO -> "И"
        SongSectionType.ENDING -> "Ф"
        SongSectionType.TAG -> "Т"
    }

private fun sectionColor(type: SongSectionType): Color =
    when (type) {
        SongSectionType.VERSE -> Color(0xFF7C3AED)
        SongSectionType.CHORUS -> Color(0xFF22C55E)
        SongSectionType.PRE_CHORUS -> Color(0xFFF59E0B)
        SongSectionType.BRIDGE -> Color(0xFF3B82F6)
        SongSectionType.INTRO -> Color(0xFF06B6D4)
        SongSectionType.ENDING -> Color(0xFFEF4444)
        SongSectionType.TAG -> Color(0xFF6B7280)
    }
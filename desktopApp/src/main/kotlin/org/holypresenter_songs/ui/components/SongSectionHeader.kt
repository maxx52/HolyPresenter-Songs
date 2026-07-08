package org.holypresenter_songs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.ui.common.color
import org.holypresenter_songs.ui.common.title

@Composable
fun SongSectionHeader(
    section: SongSection,
    onDuplicate: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(section.type.color(), CircleShape)
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = section.title(),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = "${section.slides.size} слайд(ов)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(Modifier.weight(1f))

        Text("⋮⋮")

        SongSectionMenu(
            onDuplicate = onDuplicate,
            onDelete = onDelete
        )
    }
}
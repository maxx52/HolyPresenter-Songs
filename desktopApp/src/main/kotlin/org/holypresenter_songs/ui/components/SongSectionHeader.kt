package org.holypresenter_songs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter.platform.ui.interaction.dragdrop.HolyDragHandle
import org.holypresenter_songs.domain.SongSection
import org.holypresenter_songs.ui.common.color
import org.holypresenter_songs.ui.common.title

@Composable
fun SongSectionHeader(
    section: SongSection,
    onAddToOrder: () -> Unit = {},
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
                .background(
                    section.type.color(),
                    CircleShape
                )
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            text = section.title(),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = "${section.slides.size} слайд(ов)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        TextButton(
            onClick = onAddToOrder
        ) {
            Text(
                text = "＋ В порядок"
            )
        }

        Spacer(
            modifier = Modifier.width(4.dp)
        )

        HolyDragHandle()

        SongSectionMenu(
            onDuplicate = onDuplicate,
            onDelete = onDelete
        )
    }
}
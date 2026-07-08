package org.holypresenter_songs.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.SongSlide
import androidx.compose.ui.focus.onFocusChanged

@Composable
fun SongSlideCard(
    slide: SongSlide,
    selected: Boolean,
    onSelect: () -> Unit,
    onTextChange: (String) -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onSelect()
            },
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        color =
            if (selected)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "⋮\n⋮\n⋮",
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.width(12.dp))

            BasicTextField(
                value = slide.lines.joinToString("\n"),
                onValueChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
                    .onFocusChanged {
                        if (it.isFocused) onSelect()
                    },
                minLines = 1,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
            )

            Spacer(Modifier.width(12.dp))

            SongSlideMenu(
                onDuplicate = onDuplicate,
                onDelete = onDelete
            )
        }
    }
}
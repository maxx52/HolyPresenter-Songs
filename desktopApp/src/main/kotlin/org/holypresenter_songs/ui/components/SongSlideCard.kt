package org.holypresenter_songs.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import org.holypresenter.platform.ui.interaction.dragdrop.HolyDragHandle
import org.holypresenter_songs.domain.SongSlide

@Composable
fun SongSlideCard(
    slide: SongSlide,
    selected: Boolean,
    onSelect: () -> Unit,
    onTextChange: (String) -> Unit,
    onChordsChange: (String) -> Unit,
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
            if (selected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            HolyDragHandle()

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Аккорды",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                BasicTextField(
                    value = slide.chordsEditorText(),
                    onValueChange = onChordsChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .onFocusChanged {
                            if (it.isFocused) {
                                onSelect()
                            }
                        },
                    minLines = 1,
                    textStyle =
                        MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary
                        ),
                    cursorBrush =
                        SolidColor(
                            MaterialTheme.colorScheme.primary
                        )
                )

                Text(
                    text = "Текст",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                BasicTextField(
                    value = slide.lines.joinToString("\n"),
                    onValueChange = onTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .onFocusChanged {
                            if (it.isFocused) {
                                onSelect()
                            }
                        },
                    minLines = 1,
                    textStyle =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                    cursorBrush =
                        SolidColor(
                            MaterialTheme.colorScheme.primary
                        )
                )
            }

            Spacer(Modifier.width(12.dp))

            SongSlideMenu(
                onDuplicate = onDuplicate,
                onDelete = onDelete
            )
        }
    }
}

private fun SongSlide.chordsEditorText(): String {
    if (lines.isEmpty()) {
        return ""
    }

    return List(lines.size) { lineIndex ->
        chords
            .getOrNull(lineIndex)
            .orEmpty()
    }.joinToString("\n")
}
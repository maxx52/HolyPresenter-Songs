package org.holypresenter_songs.ui.topbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.presentation.SongEditorContext

@Composable
fun SongsTopBar(
    context: SongEditorContext,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val song = context.state.song
    val editor = context.editor

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onBackClick
        ) {
            Text("← Песни")
        }

        Spacer(Modifier.width(24.dp))

        Column {
            Text(
                text = song
                    ?.metadata
                    ?.title
                    .orEmpty()
                    .ifBlank { "Без названия" },
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = song
                    ?.metadata
                    ?.author
                    .orEmpty()
                    .ifBlank { "Автор не указан" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(Modifier.weight(1f))

        TextButton(
            enabled = editor.canUndo,
            onClick = {
                editor.undo()
            }
        ) {
            Text("↶ Отменить")
        }

        Spacer(Modifier.width(8.dp))

        TextButton(
            enabled = editor.canRedo,
            onClick = {
                editor.redo()
            }
        ) {
            Text("↷ Повторить")
        }
    }
}
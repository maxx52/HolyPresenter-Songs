package org.holypresenter_songs.ui.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.editor.command.metadata.UpdateSongMetadataCommand
import org.holypresenter_songs.presentation.SongEditorContext

@Composable
fun SongsTopBar(
    context: SongEditorContext,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val song = context.state.song

    var title by remember(song?.id) {
        mutableStateOf(song?.metadata?.title.orEmpty())
    }

    var author by remember(song?.id) {
        mutableStateOf(song?.metadata?.author.orEmpty())
    }

    LaunchedEffect(
        song?.metadata?.title,
        song?.metadata?.author
    ) {
        title = song?.metadata?.title.orEmpty()
        author = song?.metadata?.author.orEmpty()
    }

    fun saveMetadata() {
        val currentSong = context.state.song ?: return

        val normalizedTitle = title.trim()
        val normalizedAuthor = author.trim()

        if (
            normalizedTitle == currentSong.metadata.title &&
            normalizedAuthor == currentSong.metadata.author
        ) {
            return
        }

        context.editor.execute(
            UpdateSongMetadataCommand(
                oldTitle = currentSong.metadata.title,
                oldAuthor = currentSong.metadata.author,
                newTitle = normalizedTitle,
                newAuthor = normalizedAuthor
            )
        )
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(
            onClick = {
                saveMetadata()
                onBackClick()
            }
        ) {
            Text("← Песни")
        }

        Spacer(Modifier.width(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
            },
            label = {
                Text("Название")
            },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )

        OutlinedTextField(
            value = author,
            onValueChange = {
                author = it
            },
            label = {
                Text("Автор")
            },
            singleLine = true,
            modifier = Modifier.weight(0.7f)
        )

        TextButton(
            enabled = context.editor.canUndo,
            onClick = {
                saveMetadata()
                context.editor.undo()
            }
        ) {
            Text("↶ Отменить")
        }

        TextButton(
            enabled = context.editor.canRedo,
            onClick = {
                saveMetadata()
                context.editor.redo()
            }
        ) {
            Text("↷ Повторить")
        }
    }
}
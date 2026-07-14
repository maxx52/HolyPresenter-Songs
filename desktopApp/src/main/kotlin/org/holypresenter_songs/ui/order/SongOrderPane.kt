package org.holypresenter_songs.ui.order

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter.platform.ui.interaction.dragdrop.HolyReorderColumn
import org.holypresenter_songs.domain.editor.command.section.MoveSectionCommand
import org.holypresenter_songs.presentation.SongEditorContext

@Composable
fun SongOrderPane(
    context: SongEditorContext,
    modifier: Modifier = Modifier
) {
    val song = context.state.song
    val sections = song?.sections.orEmpty()

    Surface(
        modifier = modifier.fillMaxHeight(),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Порядок исполнения",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Перетащите блоки для изменения порядка",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.height(16.dp))

            HolyReorderColumn(
                items = sections,
                modifier = Modifier.weight(1f),
                onMove = { fromIndex, toIndex ->
                    context.editor.execute(
                        MoveSectionCommand(
                            fromIndex = fromIndex,
                            toIndex = toIndex
                        )
                    )
                }
            ) { section, index, _ ->
                SongOrderItem(
                    number = index + 1,
                    section = section
                )

                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(12.dp))

            SongOrderDropArea()
        }
    }
}
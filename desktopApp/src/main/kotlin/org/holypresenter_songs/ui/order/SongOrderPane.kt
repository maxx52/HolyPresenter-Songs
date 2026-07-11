package org.holypresenter_songs.ui.order

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter.platform.ui.interaction.dragdrop.HolyReorderColumn
import org.holypresenter_songs.domain.Song

@Composable
fun SongOrderPane(
    song: Song?,
    modifier: Modifier = Modifier,
    onSongChanged: (Song) -> Unit = {}
) {
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

            val sections = song?.sections.orEmpty()

            HolyReorderColumn(
                items = sections,
                modifier = Modifier.weight(1f),
                onMove = { from, to ->
                    val currentSong = song ?: return@HolyReorderColumn

                    val reordered = currentSong.sections.move(from, to)

                    onSongChanged(
                        currentSong.copy(sections = reordered)
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

private fun <T> List<T>.move(from: Int, to: Int): List<T> {
    if (from == to) return this
    if (from !in indices || to !in indices) return this

    val mutable = toMutableList()
    val item = mutable.removeAt(from)
    mutable.add(to, item)
    return mutable
}
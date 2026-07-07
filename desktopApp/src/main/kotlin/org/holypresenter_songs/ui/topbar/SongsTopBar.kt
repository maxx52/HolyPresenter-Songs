package org.holypresenter_songs.ui.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.Song

@Composable
fun SongsTopBar(
    song: Song?,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onTitleClick: () -> Unit = {},
    onAuthorClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        TextButton(
            onClick = onBackClick
        ) {
            Text("← Песни")
        }
        Spacer(Modifier.width(24.dp))

        Column {
            Text(
                text = song?.metadata?.title.orEmpty().ifBlank { "Без названия" },
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.clickable {
                    onTitleClick()
                }
            )

            Text(
                text = song?.metadata?.author.orEmpty().ifBlank { "Автор не указан" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.clickable {
                    onAuthorClick()
                }
            )
        }
    }
}
package org.holypresenter_songs.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.holypresenter_songs.domain.SongSlide

@Composable
fun SongPreviewPane(
    selectedSlide: SongSlide?,
    modifier: Modifier = Modifier,
    onShowClick: () -> Unit = {}
) {
    Card(modifier = modifier.fillMaxHeight()) {
        Column(Modifier.padding(16.dp)) {
            Text("Предпросмотр (Проектор)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF111111)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedSlide?.lines?.joinToString("\n") ?: "",
                    color = Color.White
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onShowClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Показать на проекторе")
            }
        }
    }
}
package org.holypresenter_songs.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SongThemePane(
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxHeight()) {
        Column(Modifier.padding(16.dp)) {
            Text("Тема оформления", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            Text("Фон")
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text("Выбрать изображение")
            }

            Spacer(Modifier.height(24.dp))

            Text("Шрифт")
            OutlinedTextField(
                value = "Arial",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Text("Overlay")
            Slider(
                value = 0.45f,
                onValueChange = {}
            )
        }
    }
}
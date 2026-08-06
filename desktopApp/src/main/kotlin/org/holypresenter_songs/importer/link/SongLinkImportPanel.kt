package org.holypresenter_songs.importer.link

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
internal fun SongLinkImportPanel(
    importer: SongLinkImporter,
    onLoaded: (SongLinkImportData) -> Unit,
    modifier: Modifier = Modifier
) {
    var url by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = url,
                onValueChange = { newValue ->
                    url = newValue
                    errorMessage = null
                },
                enabled = !isLoading,
                singleLine = true,
                label = {
                    Text("Ссылка HolyChords")
                },
                placeholder = {
                    Text(
                        "https://holychords.pro/..."
                    )
                },
                modifier = Modifier.weight(1f)
            )

            Button(
                enabled = url.isNotBlank() && !isLoading,
                onClick = {
                    if (!importer.supports(url)) {
                        errorMessage = "Введите ссылку с сайта HolyChords."
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null

                    coroutineScope.launch {
                        when (
                            val result = importer.importSong(url)
                        ) {
                            is SongLinkImportResult.Success -> {
                                onLoaded(result.data)
                            }

                            is SongLinkImportResult.Failure -> {
                                errorMessage = result.message
                            }
                        }
                        isLoading = false
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Загрузить")
                }
            }
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}